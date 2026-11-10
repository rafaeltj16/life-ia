package com.lifeos.core_ai.engine

import com.lifeos.core_ai.provider.TaskProvider
import com.lifeos.core_ai.provider.HabitProvider
import com.lifeos.core_ai.provider.ProjectProvider
import com.lifeos.core_ai.provider.CalendarProvider
import com.lifeos.core_ai.model.DashboardSnapshot
import java.time.LocalDate

class SnapshotBuilder(
    private val taskProvider: TaskProvider,
    private val habitProvider: HabitProvider,
    private val projectProvider: ProjectProvider,
    private val calendarProvider: CalendarProvider
) {

    suspend fun build(): DashboardSnapshot {
        return DashboardSnapshot(
            tasks = taskProvider.getPendingTasks(),
            habits = habitProvider.getTodayHabits(),
            projects = projectProvider.getActiveProjects(),
            events = calendarProvider.getEvents(LocalDate.now()),
            generatedAt = System.currentTimeMillis()
        )
    }
}
