package com.example.services.sync

import android.content.Context
import android.util.Log
import com.example.core.database.AppDatabase
import com.example.core.database.SyncQueueEntity
import com.example.core.network.SupabaseServiceClient
import com.example.core.network.SupabaseTask
import com.example.core.network.SupabaseSubTask
import com.example.core.network.SupabaseProject
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncQueueProcessor private constructor(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.syncQueueDao()
    private val mutex = Mutex()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus: StateFlow<String?> = _syncStatus

    fun enqueue(userId: String, action: String, payloadObj: Any) {
        scope.launch {
            try {
                val payloadJson = when (payloadObj) {
                    is String -> payloadObj
                    is SupabaseTask -> moshi.adapter(SupabaseTask::class.java).toJson(payloadObj)
                    is SupabaseSubTask -> moshi.adapter(SupabaseSubTask::class.java).toJson(payloadObj)
                    is SupabaseProject -> moshi.adapter(SupabaseProject::class.java).toJson(payloadObj)
                    else -> payloadObj.toString()
                }

                val item = SyncQueueEntity(
                    userId = userId,
                    action = action,
                    payload = payloadJson
                )
                dao.enqueue(item)
                Log.d(TAG, "Successfully enqueued sync action: $action")
                processQueue()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to enqueue sync item", e)
            }
        }
    }

    fun processQueue() {
        scope.launch {
            mutex.withLock {
                _isSyncing.value = true
                _syncStatus.value = "Iniciando sincronização..."
                try {
                    val pending = dao.getPendingItems()
                    if (pending.isEmpty()) {
                        _syncStatus.value = "Sincronizado"
                        _isSyncing.value = false
                        return@launch
                    }

                    Log.i(TAG, "Processing sync queue: ${pending.size} items pending.")
                    var successCount = 0
                    var failCount = 0

                    for (item in pending) {
                        _syncStatus.value = "Sincronizando ${successCount + 1}/${pending.size}..."
                        val success = trySyncItem(item)
                        if (success) {
                            dao.dequeue(item.id)
                            successCount++
                        } else {
                            // If sync fails, update retry count and halt execution to preserve FIFO order
                            val updatedItem = item.copy(
                                retryCount = item.retryCount + 1,
                                lastError = "Sincronização remota indisponível"
                            )
                            dao.update(updatedItem)
                            failCount++
                            Log.w(TAG, "Halting sync queue processing. Will retry later.")
                            break
                        }
                    }

                    _syncStatus.value = if (failCount > 0) {
                        "Sincronização pausada (offline)"
                    } else {
                        "Sincronização concluída com sucesso!"
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing sync queue", e)
                    _syncStatus.value = "Erro na sincronização"
                } finally {
                    _isSyncing.value = false
                }
            }
        }
    }

    private suspend fun trySyncItem(item: SyncQueueEntity): Boolean {
        Log.d(TAG, "Attempting sync for item ID: ${item.id}, action: ${item.action}")
        return try {
            when (item.action) {
                ACTION_SAVE_TASK -> {
                    val task = moshi.adapter(SupabaseTask::class.java).fromJson(item.payload)
                        ?: throw Exception("Failed to deserialize SupabaseTask payload")
                    
                    val existingTasks = SupabaseServiceClient.getTasks(item.userId)
                    val exists = existingTasks.any { it.id == task.id }
                    if (exists) {
                        SupabaseServiceClient.updateTask(task)
                    } else {
                        SupabaseServiceClient.insertTask(task)
                    }
                    true
                }
                ACTION_DELETE_TASK -> {
                    SupabaseServiceClient.deleteTask(item.userId, item.payload)
                    true
                }
                ACTION_SAVE_SUBTASK -> {
                    val subTask = moshi.adapter(SupabaseSubTask::class.java).fromJson(item.payload)
                        ?: throw Exception("Failed to deserialize SupabaseSubTask payload")
                    
                    val existingSubTasks = SupabaseServiceClient.getSubTasks(subTask.taskId)
                    val exists = existingSubTasks.any { it.id == subTask.id }
                    if (exists) {
                        SupabaseServiceClient.updateSubTask(subTask)
                    } else {
                        SupabaseServiceClient.insertSubTask(subTask)
                    }
                    true
                }
                ACTION_DELETE_SUBTASK -> {
                    val parts = item.payload.split(":")
                    if (parts.size == 2) {
                        SupabaseServiceClient.deleteSubTask(parts[0], parts[1])
                    } else {
                        SupabaseServiceClient.deleteSubTask("", item.payload)
                    }
                    true
                }
                ACTION_SAVE_PROJECT -> {
                    val project = moshi.adapter(SupabaseProject::class.java).fromJson(item.payload)
                        ?: throw Exception("Failed to deserialize SupabaseProject payload")
                    
                    val existingProjects = SupabaseServiceClient.getProjects(item.userId)
                    val exists = existingProjects.any { it.id == project.id }
                    if (exists) {
                        SupabaseServiceClient.updateProject(project)
                    } else {
                        SupabaseServiceClient.insertProject(project)
                    }
                    true
                }
                ACTION_DELETE_PROJECT -> {
                    SupabaseServiceClient.deleteProject(item.userId, item.payload)
                    true
                }
                else -> {
                    Log.e(TAG, "Unknown sync action: ${item.action}")
                    true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sync item ${item.id} failed", e)
            false
        }
    }

    companion object {
        private const val TAG = "SyncQueueProcessor"

        const val ACTION_SAVE_TASK = "SAVE_TASK"
        const val ACTION_DELETE_TASK = "DELETE_TASK"
        const val ACTION_SAVE_SUBTASK = "SAVE_SUBTASK"
        const val ACTION_DELETE_SUBTASK = "DELETE_SUBTASK"
        const val ACTION_SAVE_PROJECT = "SAVE_PROJECT"
        const val ACTION_DELETE_PROJECT = "DELETE_PROJECT"

        @Volatile
        private var INSTANCE: SyncQueueProcessor? = null

        fun getInstance(context: Context): SyncQueueProcessor {
            return INSTANCE ?: synchronized(this) {
                val instance = SyncQueueProcessor(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
