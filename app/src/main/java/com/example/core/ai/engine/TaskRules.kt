package com.example.core.ai.engine

import com.example.core.ai.model.AIAction
import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.AIInsightType
import com.example.core.ai.model.InsightPriority
import com.example.core.ai.model.UserContext

object TaskRules {
    fun evaluateTasks(userContext: UserContext): List<AIInsight> {
        val data = userContext.dashboardData
        val total = data.todayTasks.size
        val completed = data.todayTasks.count { it.isCompleted }
        val pending = total - completed
        
        val list = mutableListOf<AIInsight>()
        if (total > 0) {
            if (pending == 0) {
                list.add(
                    AIInsight(
                        id = "rule_tasks_perfect",
                        title = "Todas as Tarefas Concluídas",
                        message = "Excelente trabalho de gerenciamento! Todas as tarefas de hoje foram concluídas com sucesso.",
                        type = AIInsightType.TASK,
                        priority = InsightPriority.HIGH,
                        action = null,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } else {
                val highPriorityUncompleted = data.todayTasks.find { !it.isCompleted && it.priority == com.example.core.common.TaskPriority.HIGH }
                if (highPriorityUncompleted != null) {
                    list.add(
                        AIInsight(
                            id = "rule_tasks_high_priority",
                            title = "Prioridade do Dia",
                            message = "Sua tarefa prioritária '${highPriorityUncompleted.title}' está aguardando conclusão.",
                            type = AIInsightType.TASK,
                            priority = InsightPriority.HIGH,
                            action = AIAction(
                                id = "action_task_focus",
                                label = "Focar na Tarefa",
                                type = "TASK",
                                payloadTitle = highPriorityUncompleted.title,
                                payloadIcon = "⚡"
                            ),
                            createdAt = System.currentTimeMillis()
                        )
                    )
                } else {
                    list.add(
                        AIInsight(
                            id = "rule_tasks_pending",
                            title = "Tarefas Restantes",
                            message = "Você tem $pending tarefas pendentes para hoje. Que tal organizar os próximos passos?",
                            type = AIInsightType.TASK,
                            priority = InsightPriority.MEDIUM,
                            action = null,
                            createdAt = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
        return list
    }
}
