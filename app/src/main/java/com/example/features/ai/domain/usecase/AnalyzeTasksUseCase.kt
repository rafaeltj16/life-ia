package com.example.features.ai.domain.usecase

import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.InsightPriority
import com.example.core.ai.model.AIInsightType
import com.example.core.ai.model.AIAction
import com.example.core.ai.model.UserContext

class AnalyzeTasksUseCase {
    operator fun invoke(userContext: UserContext): List<AIInsight> {
        val insights = mutableListOf<AIInsight>()
        val todayTasks = userContext.dashboardData.todayTasks
        val completedCount = todayTasks.count { it.isCompleted }
        val totalCount = todayTasks.size
        
        if (totalCount > 0) {
            val completionRate = completedCount.toFloat() / totalCount
            if (completionRate == 1.0f) {
                insights.add(
                    AIInsight(
                        id = "task_all_completed",
                        title = "Dia de Alta Produtividade!",
                        message = "Todas as $totalCount tarefas de hoje foram executadas. Sua lista está limpa!",
                        type = AIInsightType.TASK,
                        priority = InsightPriority.HIGH,
                        action = null,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } else if (completionRate >= 0.5f) {
                insights.add(
                    AIInsight(
                        id = "task_good_completion",
                        title = "Progresso Produtivo",
                        message = "Você completou $completedCount de $totalCount tarefas planejadas para hoje.",
                        type = AIInsightType.TASK,
                        priority = InsightPriority.MEDIUM,
                        action = null,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } else {
                insights.add(
                    AIInsight(
                        id = "task_pending",
                        title = "Lista de Tarefas Pendente",
                        message = "Você tem ${totalCount - completedCount} tarefas pendentes hoje. Foque no mais importante.",
                        type = AIInsightType.TASK,
                        priority = InsightPriority.HIGH,
                        action = AIAction(
                            id = "action_focus_task",
                            label = "Executar Tarefa Rápida",
                            type = "TASK",
                            payloadTitle = "Organizar prioridades do dia",
                            payloadIcon = "✏️"
                        ),
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        } else {
            insights.add(
                AIInsight(
                    id = "task_empty",
                    title = "Nenhuma Tarefa Hoje",
                    message = "Defina suas metas diárias para focar no que realmente importa.",
                    type = AIInsightType.TASK,
                    priority = InsightPriority.MEDIUM,
                    action = AIAction(
                        id = "action_create_task",
                        label = "Adicionar Tarefa Importante",
                        type = "TASK",
                        payloadTitle = "Planejar metas semanais",
                        payloadIcon = "🎯"
                    ),
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        return insights
    }
}
