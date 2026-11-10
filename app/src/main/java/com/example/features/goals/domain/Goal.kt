package com.example.features.goals.domain

import com.example.core.common.Habit
import com.example.core.common.Project
import com.example.core.common.Task
import java.time.LocalDate
import java.util.UUID

data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val deadline: LocalDate = LocalDate.now().plusDays(30),
    val progress: Float = 0f,
    val projects: List<Project> = emptyList(),
    val habits: List<Habit> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val category: String = "Geral",
    val priority: Int = 1,
    val status: GoalStatus = GoalStatus.IN_PROGRESS
) {
    val currentProgress: Int get() = (progress * 100).toInt()
    val targetProgress: Int get() = 100
    val targetDateMillis: Long get() = deadline.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
}

