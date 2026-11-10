package com.example.features.goals.data

import com.example.features.goals.domain.Goal
import com.example.features.goals.domain.GoalStatus
import java.time.Instant
import java.time.ZoneId

object GoalMapper {

    fun toDomain(entity: GoalEntity): Goal {
        val deadlineDate = Instant.ofEpochMilli(entity.targetDateMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val floatProgress = (entity.currentProgress.toFloat() / entity.targetProgress.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)

        return Goal(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            deadline = deadlineDate,
            progress = floatProgress,
            category = entity.category,
            priority = entity.priority,
            status = try {
                GoalStatus.valueOf(entity.statusString)
            } catch (e: Exception) {
                GoalStatus.IN_PROGRESS
            }
        )
    }

    fun toEntity(domain: Goal): GoalEntity {
        return GoalEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            category = domain.category,
            targetProgress = domain.targetProgress,
            currentProgress = domain.currentProgress,
            targetDateMillis = domain.targetDateMillis,
            priority = domain.priority,
            statusString = domain.status.name
        )
    }
}

