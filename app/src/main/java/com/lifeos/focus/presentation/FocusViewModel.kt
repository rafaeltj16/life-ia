package com.lifeos.focus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifeos.focus.domain.usecase.FinishFocusSessionUseCase
import com.lifeos.focus.domain.usecase.StartFocusSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

annotation class HiltViewModel
annotation class Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val startFocus: StartFocusSessionUseCase,
    private val finishFocus: FinishFocusSessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FocusState())
    val state = _state.asStateFlow()

    fun start(taskId: String?) {
        viewModelScope.launch {
            val session = startFocus(taskId)
            _state.value = _state.value.copy(
                running = true,
                paused = false,
                currentSession = session
            )
        }
    }

    fun finish() {
        viewModelScope.launch {
            val finishedSession = finishFocus(_state.value.currentSession)
            _state.value = _state.value.copy(
                running = false,
                paused = false,
                currentSession = finishedSession
            )
        }
    }
}
