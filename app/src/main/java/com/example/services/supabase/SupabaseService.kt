package com.example.services.supabase

import com.example.core.network.SupabaseServiceClient
import com.example.core.network.SupabaseTask

/**
 * Service package wrapper for Supabase API operations.
 */
object SupabaseService {
    suspend fun getTasks(userId: String): List<SupabaseTask> {
        return SupabaseServiceClient.getTasks(userId)
    }
}
