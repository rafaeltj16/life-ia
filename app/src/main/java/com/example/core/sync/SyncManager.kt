package com.example.core.sync

import android.content.Context
import com.example.services.sync.SyncQueueProcessor
import kotlinx.coroutines.flow.StateFlow

/**
 * Unified Core Sync Manager adhering to the modular Core architecture.
 */
class SyncManager private constructor(private val context: Context) {
    private val processor = SyncQueueProcessor.getInstance(context)

    val isSyncing: StateFlow<Boolean> = processor.isSyncing
    val syncStatus: StateFlow<String?> = processor.syncStatus

    fun enqueueSync(userId: String, action: String, payload: Any) {
        processor.enqueue(userId, action, payload)
    }

    fun syncNow() {
        processor.processQueue()
    }

    companion object {
        @Volatile
        private var INSTANCE: SyncManager? = null

        fun getInstance(context: Context): SyncManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SyncManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
