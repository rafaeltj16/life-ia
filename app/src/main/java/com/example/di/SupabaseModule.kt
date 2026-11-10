package com.example.di

import com.example.core.network.SupabaseServiceClient

object SupabaseModule {
    val supabaseClient: SupabaseServiceClient by lazy {
        SupabaseServiceClient
    }
}
