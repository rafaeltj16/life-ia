package com.example.di

import com.example.core.network.GeminiServiceClient
import com.example.services.gemini.GeminiRepository
import com.example.services.gemini.GeminiRepositoryImpl
import com.example.services.gemini.GeminiService

object GeminiModule {
    val geminiClient: GeminiServiceClient by lazy {
        GeminiServiceClient
    }

    val geminiService: GeminiService by lazy {
        GeminiService(geminiClient)
    }

    val geminiRepository: GeminiRepository by lazy {
        GeminiRepositoryImpl(geminiService)
    }
}
