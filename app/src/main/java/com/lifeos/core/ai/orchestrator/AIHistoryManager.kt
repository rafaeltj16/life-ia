package com.lifeos.core.ai.orchestrator

import com.lifeos.core.ai.models.AIRequest
import com.lifeos.core.ai.models.AIResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HistoryItem(
    val request: AIRequest,
    val response: AIResponse,
    val timestamp: Long = System.currentTimeMillis()
)

class AIHistoryManager {
    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    fun recordInteraction(request: AIRequest, response: AIResponse) {
        val newItem = HistoryItem(request, response)
        val current = _history.value.toMutableList()
        current.add(0, newItem)
        _history.value = current.take(50)
    }

    fun clear() {
        _history.value = emptyList()
    }
}
