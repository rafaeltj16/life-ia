package com.example.core.ai.engine

import com.example.core.ai.model.AIAction
import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.AIInsightType
import com.example.core.ai.model.InsightPriority
import com.example.core.ai.model.UserContext

object ProjectRules {
    fun evaluateProjects(userContext: UserContext): List<AIInsight> {
        val data = userContext.dashboardData
        val list = mutableListOf<AIInsight>()
        if (data.projects.isEmpty()) {
            list.add(
                AIInsight(
                    id = "rule_projects_empty",
                    title = "Inicie um Novo Projeto",
                    message = "Projetos ajudam a organizar metas maiores de médio prazo. Crie seu primeiro projeto para começar!",
                    type = AIInsightType.PROJECT,
                    priority = InsightPriority.LOW,
                    action = null,
                    createdAt = System.currentTimeMillis()
                )
            )
        } else {
            val project = data.projects.first()
            list.add(
                AIInsight(
                    id = "rule_projects_focus",
                    title = "Avançar no Projeto",
                    message = "Foco em '${project.name}'. Lembre-se de dar passos constantes para manter o progresso contínuo.",
                    type = AIInsightType.PROJECT,
                    priority = InsightPriority.MEDIUM,
                    action = AIAction(
                        id = "action_project_open",
                        label = "Abrir Projeto",
                        type = "SYSTEM",
                        payloadTitle = project.name,
                        payloadIcon = "📂"
                    ),
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        return list
    }
}
