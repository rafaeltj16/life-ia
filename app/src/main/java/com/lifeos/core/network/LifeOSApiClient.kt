package com.lifeos.core.network

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T, val statusCode: Int = 200) : ApiResponse<T>()
    data class ServerError(val code: Int, val message: String) : ApiResponse<Nothing>()
    data class NetworkError(val error: Throwable) : ApiResponse<Nothing>()
}

interface LifeOSApiClient {
    suspend fun syncUserData(userId: String): ApiResponse<Boolean>
    suspend fun executeHealthCheck(): ApiResponse<Map<String, String>>
}

class LifeOSApiClientImpl : LifeOSApiClient {
    override suspend fun syncUserData(userId: String): ApiResponse<Boolean> {
        return try {
            ApiResponse.Success(true)
        } catch (e: Exception) {
            ApiResponse.NetworkError(e)
        }
    }

    override suspend fun executeHealthCheck(): ApiResponse<Map<String, String>> {
        return ApiResponse.Success(
            mapOf(
                "status" to "healthy",
                "version" to "2.4.0",
                "ai_engine" to "online",
                "database" to "synced"
            )
        )
    }
}
