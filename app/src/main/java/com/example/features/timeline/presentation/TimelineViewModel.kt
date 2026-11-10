package com.example.features.timeline.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.timeline.data.TimelineRepository
import com.example.features.timeline.data.TimelineRepositoryImpl
import com.example.features.timeline.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class TimelineUiState(
    val items: List<TimelineItem> = emptyList(),
    val filteredItems: List<TimelineItem> = emptyList(),
    val selectedFilter: TimelineFilter = TimelineFilter.ALL,
    val isLoading: Boolean = false
)

class TimelineViewModel(
    private val repository: TimelineRepository = TimelineRepositoryImpl(),
    private val getTimelineItemsUseCase: GetTimelineItemsUseCase = GetTimelineItemsUseCase(repository),
    private val builder: TimelineBuilder = TimelineBuilder()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    init {
        loadTimeline()
    }

    private fun loadTimeline() {
        viewModelScope.launch {
            getTimelineItemsUseCase().collectLatest { items ->
                val filtered = builder.filter(items, _uiState.value.selectedFilter)
                _uiState.value = _uiState.value.copy(
                    items = items,
                    filteredItems = filtered
                )
            }
        }
    }

    fun setFilter(filter: TimelineFilter) {
        val filtered = builder.filter(_uiState.value.items, filter)
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            filteredItems = filtered
        )
    }
}
