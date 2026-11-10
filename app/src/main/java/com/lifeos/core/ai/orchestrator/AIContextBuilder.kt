package com.lifeos.core.ai.orchestrator

import com.example.core.common.CalendarEvent
import com.example.core.common.Habit
import com.example.core.common.Task
import com.example.features.goals.domain.Goal
import com.lifeos.core.ai.models.AIContext
import com.lifeos.focus.domain.model.FocusSession
import java.time.LocalDateTime

class AIContextBuilder {
    private var currentTime: LocalDateTime = LocalDateTime.now()
    private var lifeScore: Int = 75
    private var activeGoal: Goal? = null
    private var todayTasks: List<Task> = emptyList()
    private var todayHabits: List<Habit> = emptyList()
    private var calendarEvents: List<CalendarEvent> = emptyList()
    private var activeFocusSession: FocusSession? = null
    private var productivityScore: Int = 80

    fun setCurrentTime(time: LocalDateTime) = apply { this.currentTime = time }
    fun setLifeScore(score: Int) = apply { this.lifeScore = score }
    fun setActiveGoal(goal: Goal?) = apply { this.activeGoal = goal }
    fun setTodayTasks(tasks: List<Task>) = apply { this.todayTasks = tasks }
    fun setTodayHabits(habits: List<Habit>) = apply { this.todayHabits = habits }
    fun setCalendarEvents(events: List<CalendarEvent>) = apply { this.calendarEvents = events }
    fun setActiveFocusSession(session: FocusSession?) = apply { this.activeFocusSession = session }
    fun setProductivityScore(score: Int) = apply { this.productivityScore = score }

    fun build(): AIContext {
        return AIContext(
            currentTime = currentTime,
            lifeScore = lifeScore,
            activeGoal = activeGoal,
            todayTasks = todayTasks,
            todayHabits = todayHabits,
            calendarEvents = calendarEvents,
            activeFocusSession = activeFocusSession,
            productivityScore = productivityScore
        )
    }
}
