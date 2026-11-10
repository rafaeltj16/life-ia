package com.example.di

import android.content.Context

class AppModule(private val context: Context) {
    val databaseModule: DatabaseModule by lazy {
        DatabaseModule(context)
    }

    val repositoryModule: RepositoryModule by lazy {
        RepositoryModule(context, databaseModule)
    }

    val networkModule: NetworkModule by lazy {
        NetworkModule
    }

    val supabaseModule: SupabaseModule by lazy {
        SupabaseModule
    }

    val geminiModule: GeminiModule by lazy {
        GeminiModule
    }
}
