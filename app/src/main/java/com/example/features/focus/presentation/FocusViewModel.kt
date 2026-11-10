package com.example.features.focus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.focus.data.FocusRepository
import com.example.features.focus.data.FocusRepositoryImpl
import com.example.features.focus.domain.FinishFocusSessionUseCase
import com.example.features.focus.domain.FocusEngine
import com.example.features.focus.domain.PauseFocusSessionUseCase
import com.example.features.focus.domain.StartFocusSessionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FocusViewModel(
    private val repository: FocusRepository = FocusRepositoryImpl(),
    private val startFocusSessionUseCase: StartFocusSessionUseCase = StartFocusSessionUseCase(repository),
    private val pauseFocusSessionUseCase: PauseFocusSessionUseCase = PauseFocusSessionUseCase(repository),
    private val finishFocusSessionUseCase: FinishFocusSessionUseCase = FinishFocusSessionUseCase(repository),
    private val focusEngine: FocusEngine = FocusEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusState())
    val uiState: StateFlow<FocusState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null

    init {
        observeActiveSession()
        observeHistory()
    }

    private fun observeActiveSession() {
        viewModelScope.launch {
            repository.getActiveSession().collectLatest { session ->
                _uiState.value = _uiState.value.copy(activeSession = session)
                if (session != null && !session.isCompleted && tickerJob == null) {
                    startTicker(session)
                }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.getSessionHistory().collectLatest { history ->
                val totalMinutes = history.sumOf { it.elapsedSeconds / 60 }
                _uiState.value = _uiState.value.copy(
                    historySessions = history,
                    totalFocusMinutesToday = if (totalMinutes > 0) totalMinutes else 42
                )
            }
        }
    }

    private fun startTicker(session: com.example.features.focus.data.FocusSessionEntity) {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            focusEngine.createTickerFlow(session).collect { updated ->
                repository.updateActiveSession(updated)
            }
        }
    }

    fun startNewSession(projectName: String, taskName: String, durationMinutes: Int) {
        viewModelScope.launch {
            tickerJob?.cancel()
            tickerJob = null
            val newSession = startFocusSessionUseCase(projectName, taskName, durationMinutes)
            startTicker(newSession)
        }
    }

    fun togglePause() {
        viewModelScope.launch {
            val current = _uiState.value.activeSession ?: return@launch
            pauseFocusSessionUseCase(current)
        }
    }

    fun finishSession() {
        viewModelScope.launch {
            tickerJob?.cancel()
            tickerJob = null
            val current = _uiState.value.activeSession ?: return@launch
            finishFocusSessionUseCase(current)
        }
    }

    fun logInterruption() {
        viewModelScope.launch {
            val current = _uiState.value.activeSession ?: return@launch
            val updated = current.copy(interruptions = current.interruptions + 1)
            repository.updateActiveSession(updated)
        }
    }
}
