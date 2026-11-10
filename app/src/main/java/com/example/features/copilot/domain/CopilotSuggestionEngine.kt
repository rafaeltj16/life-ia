package com.example.features.copilot.domain

import com.example.features.copilot.data.CopilotRepository
import com.example.features.copilot.data.CopilotSuggestion
import kotlinx.coroutines.flow.Flow

class CopilotSuggestionEngine(
    private val repository: CopilotRepository
) {
    fun getSuggestions(): Flow<List<CopilotSuggestion>> = repository.getCopilotSuggestions()

    suspend fun dismissSuggestion(id: String) {
        repository.dismissSuggestion(id)
    }
}
