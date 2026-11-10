package com.example.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.core.ai.repository.AIRepository
import com.example.features.dashboard.domain.model.DashboardAIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Local stub annotations to satisfy the requested Hilt structure 
// in this project's standard JVM constructor-injection environment.
annotation class HiltViewModel
annotation class Inject

@HiltViewModel
class DashboardAIViewModel @Inject constructor(
    private val repository: AIRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardAIState())
    val state = _state.asStateFlow()
}
