package com.example.features.timeline.data

import com.example.features.timeline.domain.TimelineItem
import kotlinx.coroutines.flow.Flow

interface TimelineRepository {
    fun getTimelineItems(): Flow<List<TimelineItem>>
    suspend fun addTimelineItem(item: TimelineItem)
}
