package com.example.core.ai.factory

import com.example.core.ai.model.*

object AIInsightFactory {

    fun overdueTask(
        total: Int
    ) = AIInsight(
        id = "overdue_tasks",
        title = "Existem tarefas atrasadas",
        message = "Você possui $total tarefas vencidas.",
        type = AIInsightType.TASK,
        priority = InsightPriority.HIGH
    )

    fun lowWater() = AIInsight(
        id = "water",
        title = "Hora de beber água",
        message = "Sua hidratação está abaixo da meta.",
        type = AIInsightType.HEALTH,
        priority = InsightPriority.MEDIUM
    )

    fun inactiveProject() = AIInsight(
        id = "project",
        title = "Projeto parado",
        message = "Existe um projeto sem progresso.",
        type = AIInsightType.PROJECT,
        priority = InsightPriority.HIGH
    )
}
