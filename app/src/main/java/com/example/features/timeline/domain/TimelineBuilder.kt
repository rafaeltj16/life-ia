package com.example.features.timeline.domain

import com.example.core.ai.model.AIRecommendation
import com.example.core.common.CalendarEvent
import com.example.core.common.Habit
import com.example.core.common.Task

class TimelineBuilder {

    fun build(
        tasks: List<Task>,
        habits: List<Habit>,
        events: List<CalendarEvent>,
        recommendations: List<AIRecommendation>
    ): List<TimelineItem> {

        val items = mutableListOf<TimelineItem>()

        tasks.forEach {
            items += TimelineItem.Task(it)
        }

        habits.forEach {
            items += TimelineItem.Habit(it)
        }

        events.forEach {
            items += TimelineItem.Calendar(it)
        }

        recommendations.forEach {
            items += TimelineItem.AIRecommendation(it)
        }

        return items.sortedBy {
            when (it) {
                is TimelineItem.Calendar -> it.event.startTime
                else -> null
            }
        }
    }

    fun filter(items: List<TimelineItem>, filter: TimelineFilter): List<TimelineItem> {
        return items.filter { item ->
            when (filter) {
                TimelineFilter.ALL -> true
                TimelineFilter.FOCUS_SESSIONS -> item is TimelineItem.Focus
                TimelineFilter.TASKS -> item is TimelineItem.Task
                TimelineFilter.HABITS -> item is TimelineItem.Habit
                TimelineFilter.PROJECTS -> item is TimelineItem.Calendar || item is TimelineItem.AIRecommendation
                TimelineFilter.GOALS -> item is TimelineItem.Goal
            }
        }.sortedByDescending { it.timestamp }
    }
}

