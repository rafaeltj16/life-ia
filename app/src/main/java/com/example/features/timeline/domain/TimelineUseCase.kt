package com.example.features.timeline.domain

import com.example.features.timeline.data.TimelineRepository
import kotlinx.coroutines.flow.Flow

class GetTimelineItemsUseCase(private val repository: TimelineRepository) {
    operator fun invoke(): Flow<List<TimelineItem>> = repository.getTimelineItems()
}

class AddTimelineItemUseCase(private val repository: TimelineRepository) {
    suspend operator fun invoke(item: TimelineItem) = repository.addTimelineItem(item)
}
