package com.example.features.timeline.domain

import com.example.core.ai.model.AIRecommendation
import com.example.core.common.CalendarEvent
import com.example.core.common.Habit
import com.example.core.common.Task
import com.example.features.goals.domain.Goal
import com.lifeos.focus.domain.model.FocusSession
import java.time.LocalDateTime

sealed class TimelineItem {

    data class Task(
        val task: com.example.core.common.Task
    ) : TimelineItem()

    data class Habit(
        val habit: com.example.core.common.Habit
    ) : TimelineItem()

    data class Calendar(
        val event: CalendarEvent
    ) : TimelineItem()

    data class Focus(
        val session: FocusSession
    ) : TimelineItem()

    data class Goal(
        val goal: com.example.features.goals.domain.Goal
    ) : TimelineItem()

    data class AIRecommendation(
        val recommendation: com.example.core.ai.model.AIRecommendation
    ) : TimelineItem()

    val id: String
        get() = when (this) {
            is Task -> task.id
            is Habit -> habit.id
            is Calendar -> event.id
            is Focus -> session.id
            is Goal -> goal.id
            is AIRecommendation -> recommendation.title
        }

    val title: String
        get() = when (this) {
            is Task -> task.title
            is Habit -> habit.name
            is Calendar -> event.title
            is Focus -> session.taskId ?: "Sessão de Foco"
            is Goal -> goal.title
            is AIRecommendation -> recommendation.title
        }

    val description: String
        get() = when (this) {
            is Task -> "Categoria: ${task.category}"
            is Habit -> "Sequência de ${habit.streak} dias"
            is Calendar -> event.description
            is Focus -> "Sessão concluída com sucesso"
            is Goal -> goal.description
            is AIRecommendation -> recommendation.description
        }

    val timestamp: LocalDateTime
        get() = when (this) {
            is Task -> LocalDateTime.now()
            is Habit -> LocalDateTime.now()
            is Calendar -> LocalDateTime.now()
            is Focus -> LocalDateTime.now()
            is Goal -> LocalDateTime.now()
            is AIRecommendation -> LocalDateTime.now()
        }

    val icon: String
        get() = when (this) {
            is Task -> "✅"
            is Habit -> habit.icon
            is Calendar -> "📅"
            is Focus -> "🎯"
            is Goal -> "🚩"
            is AIRecommendation -> "🤖"
        }

    val scoreOrBadge: String?
        get() = when (this) {
            is Task -> "+50 XP"
            is Habit -> "Sequência: ${habit.streak}d"
            is Calendar -> null
            is Focus -> "Score: 97"
            is Goal -> "${goal.currentProgress}%"
            is AIRecommendation -> "AI Insight"
        }
}

