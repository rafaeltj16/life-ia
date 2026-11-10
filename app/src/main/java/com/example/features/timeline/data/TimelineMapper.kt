package com.example.features.timeline.data

import com.example.core.ai.model.AIRecommendation
import com.example.core.common.CalendarEvent
import com.example.core.common.Habit
import com.example.core.common.Task
import com.example.features.goals.domain.Goal
import com.example.features.timeline.domain.TimelineItem
import com.lifeos.focus.domain.model.FocusSession
import java.time.Instant

data class TimelineEntity(
    val id: String,
    val title: String,
    val description: String,
    val timestampIso: String,
    val typeString: String,
    val icon: String,
    val scoreOrBadge: String?
)

object TimelineMapper {

    fun toDomain(entity: TimelineEntity): TimelineItem {
        return when (entity.typeString) {
            "TASK" -> TimelineItem.Task(
                Task(id = entity.id, title = entity.title, category = entity.description.ifBlank { "Geral" }, dueDate = "Hoje")
            )
            "HABIT" -> TimelineItem.Habit(
                Habit(id = entity.id, name = entity.title, streak = 8, icon = entity.icon)
            )
            "CALENDAR" -> TimelineItem.Calendar(
                CalendarEvent(id = entity.id, title = entity.title, time = "10:00", category = "Geral", description = entity.description)
            )
            "GOAL" -> TimelineItem.Goal(
                Goal(id = entity.id, title = entity.title, description = entity.description)
            )
            "AI_RECOMMENDATION" -> TimelineItem.AIRecommendation(
                AIRecommendation(title = entity.title, description = entity.description, confidence = 0.9f, action = null)
            )
            else -> TimelineItem.Focus(
                FocusSession(id = entity.id, taskId = entity.title, projectId = null, startedAt = Instant.now())
            )
        }
    }

    fun toEntity(domain: TimelineItem): TimelineEntity {
        val typeStr = when (domain) {
            is TimelineItem.Task -> "TASK"
            is TimelineItem.Habit -> "HABIT"
            is TimelineItem.Calendar -> "CALENDAR"
            is TimelineItem.Focus -> "FOCUS"
            is TimelineItem.Goal -> "GOAL"
            is TimelineItem.AIRecommendation -> "AI_RECOMMENDATION"
        }
        return TimelineEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            timestampIso = domain.timestamp.toString(),
            typeString = typeStr,
            icon = domain.icon,
            scoreOrBadge = domain.scoreOrBadge
        )
    }
}

