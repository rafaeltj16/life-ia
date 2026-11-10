package com.example.core.network

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.core.common.UserDashboardMetrics
import com.example.features.dashboard.data.remote.SupabaseDashboardMetricsDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.PATCH
import retrofit2.http.DELETE
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import java.util.UUID

// --- Supabase REST API Request and Response Structures ---

data class SupabaseSignUpRequest(
    val email: String,
    val password: String,
    val data: Map<String, String>? = null
)

data class SupabaseSignInRequest(
    val email: String,
    val password: String
)

data class SupabaseUser(
    val id: String,
    val email: String?,
    @field:Json(name = "user_metadata") val userMetadata: Map<String, String>?
)

data class SupabaseAuthResponse(
    @field:Json(name = "access_token") val accessToken: String?,
    @field:Json(name = "expires_in") val expiresIn: Long?,
    val user: SupabaseUser?
)

data class SupabaseRecoverRequest(
    val email: String
)

data class SupabaseTask(
    val id: String,
    @field:Json(name = "user_id") val userId: String,
    val title: String,
    val description: String? = null,
    val status: String,
    val priority: String,
    val category: String,
    @field:Json(name = "project_id") val projectId: String? = null,
    @field:Json(name = "due_date") val dueDate: String? = null,
    @field:Json(name = "completed_at") val completedAt: String? = null,
    val favorite: Boolean = false,
    @field:Json(name = "created_at") val createdAt: String? = null,
    @field:Json(name = "updated_at") val updatedAt: String? = null
)

data class SupabaseSubTask(
    val id: String,
    @field:Json(name = "task_id") val taskId: String,
    val title: String,
    val completed: Boolean
)

data class SupabaseProject(
    val id: String,
    val name: String,
    val color: String,
    @field:Json(name = "user_id") val userId: String? = null
)

interface SupabaseApiEndpoint {
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body request: SupabaseSignUpRequest
    ): SupabaseAuthResponse

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Header("apikey") apiKey: String,
        @Body request: SupabaseSignInRequest
    ): SupabaseAuthResponse

    @POST("auth/v1/recover")
    suspend fun recover(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body request: SupabaseRecoverRequest
    )

    @GET("rest/v1/user_dashboard_metrics")
    suspend fun getUserDashboardMetrics(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("user_id") userId: String
    ): List<SupabaseDashboardMetricsDto>

    @POST("rest/v1/user_dashboard_metrics")
    suspend fun saveUserDashboardMetrics(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates",
        @Body metrics: SupabaseDashboardMetricsDto
    )

    @GET("rest/v1/tasks")
    suspend fun getTasks(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("user_id") userIdQuery: String
    ): List<SupabaseTask>

    @POST("rest/v1/tasks")
    suspend fun insertTask(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body task: SupabaseTask
    )

    @POST("rest/v1/tasks")
    suspend fun insertTasks(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body tasks: List<SupabaseTask>
    )

    @PATCH("rest/v1/tasks")
    suspend fun updateTask(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idQuery: String,
        @Body task: SupabaseTask
    )

    @DELETE("rest/v1/tasks")
    suspend fun deleteTask(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idQuery: String
    )

    @GET("rest/v1/subtasks")
    suspend fun getSubTasks(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("task_id") taskIdQuery: String
    ): List<SupabaseSubTask>

    @POST("rest/v1/subtasks")
    suspend fun insertSubTask(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body subTask: SupabaseSubTask
    )

    @PATCH("rest/v1/subtasks")
    suspend fun updateSubTask(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idQuery: String,
        @Body subTask: SupabaseSubTask
    )

    @DELETE("rest/v1/subtasks")
    suspend fun deleteSubTask(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idQuery: String
    )

    @GET("rest/v1/projects")
    suspend fun getProjects(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("user_id") userIdQuery: String
    ): List<SupabaseProject>

    @POST("rest/v1/projects")
    suspend fun insertProject(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body project: SupabaseProject
    )

    @PATCH("rest/v1/projects")
    suspend fun updateProject(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idQuery: String,
        @Body project: SupabaseProject
    )

    @DELETE("rest/v1/projects")
    suspend fun deleteProject(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idQuery: String
    )
}

object SupabaseServiceClient {
    private const val TAG = "SupabaseService"

    // Optional variables that user can configure in build.gradle / secrets
    // Since we want the app to be bulletproof, we check for these keys dynamically
    private val SUPABASE_URL: String? by lazy {
        try {
            // Check if they are injected via BuildConfig
            val field = BuildConfig::class.java.getField("SUPABASE_URL")
            field.get(null) as? String
        } catch (e: Exception) {
            null
        }
    }

    private val SUPABASE_ANON_KEY: String? by lazy {
        try {
            val field = BuildConfig::class.java.getField("SUPABASE_ANON_KEY")
            field.get(null) as? String
        } catch (e: Exception) {
            null
        }
    }

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val api: SupabaseApiEndpoint? by lazy {
        val url = SUPABASE_URL
        if (url.isNullOrBlank() || !url.startsWith("http")) {
            Log.w(TAG, "Supabase URL is not configured or invalid. Fallback simulated authentication will be active.")
            null
        } else {
            try {
                Retrofit.Builder()
                    .baseUrl(if (url.endsWith("/")) url else "$url/")
                    .client(okHttpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(SupabaseApiEndpoint::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Supabase Retrofit client", e)
                null
            }
        }
    }

    // Live list of registered mock users to support rich stateful offline behavior if Supabase is offline/unconfigured
    private val localMockUsers = mutableMapOf<String, Pair<String, String>>(
        "user@lifeos.ai" to ("Usuário LifeOS" to "123456")
    )

    suspend fun authenticate(email: String, password: String): SupabaseAuthResponse = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val request = SupabaseSignInRequest(email, password)
                return@withContext endpoint.signIn(apiKey, request)
            } catch (e: Exception) {
                Log.e(TAG, "Error authenticating with Supabase. Attempting local simulation.", e)
                throw e
            }
        } else {
            // Local fallback simulation
            kotlinx.coroutines.delay(1000)
            val userRecord = localMockUsers[email.lowercase().trim()]
            if (userRecord != null && userRecord.second == password) {
                return@withContext SupabaseAuthResponse(
                    accessToken = "mock_token_jwt",
                    expiresIn = 3600,
                    user = SupabaseUser(
                        id = "mock-uuid-1234",
                        email = email,
                        userMetadata = mapOf("name" to userRecord.first)
                    )
                )
            } else {
                throw Exception("Credenciais inválidas ou e-mail não registrado localmente.")
            }
        }
    }

    suspend fun register(name: String, email: String, password: String): SupabaseAuthResponse = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val request = SupabaseSignUpRequest(
                    email = email,
                    password = password,
                    data = mapOf("name" to name)
                )
                val authHeader = "Bearer $apiKey"
                return@withContext endpoint.signUp(apiKey, authHeader, request)
            } catch (e: Exception) {
                Log.e(TAG, "Error signing up with Supabase. Attempting local simulation.", e)
                throw e
            }
        } else {
            // Local fallback simulation
            kotlinx.coroutines.delay(1000)
            val cleanEmail = email.lowercase().trim()
            if (localMockUsers.containsKey(cleanEmail)) {
                throw Exception("Este e-mail já está em uso.")
            }
            if (password.length < 6) {
                throw Exception("A senha deve conter pelo menos 6 caracteres.")
            }
            localMockUsers[cleanEmail] = name to password
            return@withContext SupabaseAuthResponse(
                accessToken = "mock_token_jwt",
                expiresIn = 3600,
                user = SupabaseUser(
                    id = "mock-uuid-new",
                    email = email,
                    userMetadata = mapOf("name" to name)
                )
            )
        }
    }

    suspend fun recoverPassword(email: String) = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            val authHeader = "Bearer $apiKey"
            endpoint.recover(apiKey, authHeader, SupabaseRecoverRequest(email))
        } else {
            kotlinx.coroutines.delay(800)
            val cleanEmail = email.lowercase().trim()
            if (!localMockUsers.containsKey(cleanEmail)) {
                throw Exception("E-mail não encontrado no sistema.")
            }
        }
    }

    private val localMetrics = mutableMapOf<String, SupabaseDashboardMetricsDto>()

    private fun getDefaultMetrics(userId: String): SupabaseDashboardMetricsDto {
        return SupabaseDashboardMetricsDto(
            userId = userId,
            tasksToday = 8,
            completedToday = 0,
            habitStreak = 18,
            weeklyGoal = 82,
            pendingProjects = 4,
            lastAiSummary = "Bom dia Rafael!\n\nHoje você possui:\n✔ 8 tarefas\n✔ 2 reuniões\n✔ Academia às 18h\n✔ Meta semanal em 82%\n\nMinha recomendação:\nFinalize primeiro as tarefas prioritárias e deixe as de baixa prioridade para depois do almoço.",
            updatedAt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        )
    }

    suspend fun getDashboardMetrics(userId: String): SupabaseDashboardMetricsDto = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                val list = endpoint.getUserDashboardMetrics(apiKey, authHeader, userId)
                if (list.isNotEmpty()) {
                    return@withContext list[0]
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting metrics from Supabase, returning local metrics", e)
            }
        }

        // Local simulation / fallback
        return@withContext localMetrics.getOrPut(userId) { getDefaultMetrics(userId) }
    }

    suspend fun saveDashboardMetrics(metrics: SupabaseDashboardMetricsDto): Unit = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        // Save locally first
        localMetrics[metrics.userId] = metrics

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                endpoint.saveUserDashboardMetrics(apiKey, authHeader, metrics = metrics)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving metrics to Supabase", e)
            }
        }
    }

    private val localTasksTable = mutableMapOf<String, MutableList<SupabaseTask>>()

    suspend fun getTasks(userId: String): List<SupabaseTask> = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                return@withContext endpoint.getTasks(apiKey, authHeader, "eq.$userId")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks from Supabase, falling back to local simulated tasks", e)
            }
        }

        // Return simulated local tasks
        return@withContext localTasksTable.getOrPut(userId) {
            mutableListOf(
                SupabaseTask(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    title = "Comprar combustível",
                    description = "Casa",
                    status = "TO_DO",
                    priority = "MEDIUM",
                    category = "Pessoal",
                    projectId = null,
                    dueDate = "Hoje",
                    favorite = true,
                    createdAt = "2026-07-02T12:00:00Z",
                    updatedAt = "2026-07-02T12:00:00Z"
                ),
                SupabaseTask(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    title = "Estudar Kotlin",
                    description = "Android,IA",
                    status = "TO_DO",
                    priority = "HIGH",
                    category = "Estudos",
                    projectId = null,
                    dueDate = "Amanhã",
                    favorite = true,
                    createdAt = "2026-07-02T12:00:00Z",
                    updatedAt = "2026-07-02T12:00:00Z"
                )
            )
        }
    }

    suspend fun insertTask(task: SupabaseTask): Unit = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        // Save locally first
        val list = localTasksTable.getOrPut(task.userId) { mutableListOf() }
        list.add(task)

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                endpoint.insertTask(apiKey, authHeader, task)
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting task into Supabase", e)
            }
        }
    }

    suspend fun updateTask(task: SupabaseTask): Unit = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        // Update locally first
        val list = localTasksTable.getOrPut(task.userId) { mutableListOf() }
        val index = list.indexOfFirst { it.id == task.id }
        if (index != -1) {
            list[index] = task
        } else {
            list.add(task)
        }

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                endpoint.updateTask(apiKey, authHeader, "eq.${task.id}", task)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task in Supabase", e)
            }
        }
    }

    suspend fun deleteTask(userId: String, taskId: String): Unit = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        // Delete locally first
        val list = localTasksTable[userId]
        list?.removeAll { it.id == taskId }

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                endpoint.deleteTask(apiKey, authHeader, "eq.$taskId")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting task from Supabase", e)
            }
        }
    }

    private val localSubTasksTable = mutableMapOf<String, MutableList<SupabaseSubTask>>()

    suspend fun getSubTasks(taskId: String): List<SupabaseSubTask> = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                return@withContext endpoint.getSubTasks(apiKey, authHeader, "eq.$taskId")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching subtasks from Supabase, falling back to local simulated", e)
            }
        }

        return@withContext localSubTasksTable.getOrPut(taskId) { mutableListOf() }
    }

    suspend fun insertSubTask(subTask: SupabaseSubTask): Unit = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        val list = localSubTasksTable.getOrPut(subTask.taskId) { mutableListOf() }
        list.add(subTask)

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                endpoint.insertSubTask(apiKey, authHeader, subTask)
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting subtask into Supabase", e)
            }
        }
    }

    suspend fun updateSubTask(subTask: SupabaseSubTask): Unit = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        val list = localSubTasksTable.getOrPut(subTask.taskId) { mutableListOf() }
        val index = list.indexOfFirst { it.id == subTask.id }
        if (index != -1) {
            list[index] = subTask
        } else {
            list.add(subTask)
        }

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                endpoint.updateSubTask(apiKey, authHeader, "eq.${subTask.id}", subTask)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating subtask in Supabase", e)
            }
        }
    }

    suspend fun deleteSubTask(taskId: String, subTaskId: String): Unit = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        val list = localSubTasksTable[taskId]
        list?.removeAll { it.id == subTaskId }

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                endpoint.deleteSubTask(apiKey, authHeader, "eq.$subTaskId")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting subtask from Supabase", e)
            }
        }
    }

    private val localProjectsTable = mutableMapOf<String, MutableList<SupabaseProject>>()

    suspend fun getProjects(userId: String): List<SupabaseProject> = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                return@withContext endpoint.getProjects(apiKey, authHeader, "eq.$userId")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching projects from Supabase, falling back to local simulated projects", e)
            }
        }

        return@withContext localProjectsTable.getOrPut(userId) {
            mutableListOf(
                SupabaseProject(id = "p1", name = "🚀 LifeOS AI Framework", color = "#7E57C2", userId = userId),
                SupabaseProject(id = "p2", name = "📚 Mestrado Acadêmico", color = "#29B6F6", userId = userId),
                SupabaseProject(id = "p3", name = "🏡 Reforma da Casa", color = "#FFA726", userId = userId)
            )
        }
    }

    suspend fun insertProject(project: SupabaseProject): Unit = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        val list = localProjectsTable.getOrPut(project.userId ?: "") { mutableListOf() }
        list.add(project)

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                endpoint.insertProject(apiKey, authHeader, project)
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting project into Supabase", e)
            }
        }
    }

    suspend fun updateProject(project: SupabaseProject): Unit = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        val list = localProjectsTable.getOrPut(project.userId ?: "") { mutableListOf() }
        val index = list.indexOfFirst { it.id == project.id }
        if (index != -1) {
            list[index] = project
        } else {
            list.add(project)
        }

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                endpoint.updateProject(apiKey, authHeader, "eq.${project.id}", project)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating project in Supabase", e)
            }
        }
    }

    suspend fun deleteProject(userId: String, projectId: String): Unit = withContext(Dispatchers.IO) {
        val endpoint = api
        val apiKey = SUPABASE_ANON_KEY

        val list = localProjectsTable[userId]
        list?.removeAll { it.id == projectId }

        if (endpoint != null && !apiKey.isNullOrBlank()) {
            try {
                val authHeader = "Bearer $apiKey"
                endpoint.deleteProject(apiKey, authHeader, "eq.$projectId")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting project from Supabase", e)
            }
        }
    }
}
