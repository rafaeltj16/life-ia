package com.example.core.ai.engine

import com.example.core.ai.model.*
import com.example.features.dashboard.domain.model.DashboardData as LegacyDashboardData

class AIRulesEngine {

    fun generateInsights(
        data: DashboardData
    ): List<AIInsight> {

        val insights = mutableListOf<AIInsight>()

        if (data.overdueTasks > 3) {

            insights.add(

                AIInsight(

                    id = "tasks",

                    title = "Existem tarefas atrasadas",

                    message = "Você possui ${data.overdueTasks} tarefas vencidas.",

                    type = AIInsightType.TASK,

                    priority = InsightPriority.HIGH

                )
            )
        }

        if (data.waterPercentage < 60) {

            insights.add(

                AIInsight(

                    id = "water",

                    title = "Hora de beber água",

                    message = "Sua hidratação está abaixo da meta.",

                    type = AIInsightType.HEALTH,

                    priority = InsightPriority.MEDIUM

                )
            )
        }

        if (data.inactiveProjects > 0) {

            insights.add(

                AIInsight(

                    id = "project",

                    title = "Projeto parado",

                    message = "Você possui projetos sem progresso.",

                    type = AIInsightType.PROJECT,

                    priority = InsightPriority.HIGH

                )
            )
        }

        return insights.sortedByDescending {

            when (it.priority) {

                InsightPriority.CRITICAL -> 4

                InsightPriority.HIGH -> 3

                InsightPriority.MEDIUM -> 2

                InsightPriority.LOW -> 1
            }
        }
    }

    companion object {
        fun evaluateFinanceRule(userContext: UserContext): AIInsight? {
            val data = userContext.dashboardData
            val balance = data.financeSummary.balance
            return if (balance < 0) {
                AIInsight(
                    id = "rule_finance_negative",
                    title = "Atenção ao Saldo Negativo",
                    message = "Seu saldo atual está negativo (R$ $balance). Considere pausar novas compras e priorizar o controle de gastos.",
                    type = AIInsightType.FINANCE,
                    priority = InsightPriority.HIGH,
                    action = AIAction(
                        id = "action_add_income",
                        label = "Registrar Receita",
                        type = "FINANCE",
                        payloadTitle = "Freelance / Extra",
                        payloadIcon = "💰"
                    ),
                    createdAt = System.currentTimeMillis()
                )
            } else if (balance > 1000) {
                AIInsight(
                    id = "rule_finance_invest",
                    title = "Oportunidade de Investimento",
                    message = "Seu saldo de R$ $balance está saudável. Que tal investir uma parcela disso hoje?",
                    type = AIInsightType.FINANCE,
                    priority = InsightPriority.MEDIUM,
                    action = AIAction(
                        id = "action_invest",
                        label = "Simular Investimento",
                        type = "FINANCE",
                        payloadTitle = "Aporte Mensal R$ 200",
                        payloadIcon = "🏦"
                    ),
                    createdAt = System.currentTimeMillis()
                )
            } else {
                null
            }
        }

        fun processRules(userContext: UserContext): List<AIInsight> {
            val insights = mutableListOf<AIInsight>()
            
            evaluateFinanceRule(userContext)?.let { insights.add(it) }
            HabitRules.evaluateStreakRule(userContext)?.let { insights.add(it) }
            
            return insights
        }

        fun generateInsights(dashboard: LegacyDashboardData): List<AIInsight> {
            val userContext = UserContext.from(dashboard, emptyList())
            val habitsInsights = HabitRules.evaluateHabits(userContext)
            val tasksInsights = TaskRules.evaluateTasks(userContext)
            val projectsInsights = ProjectRules.evaluateProjects(userContext)
            val healthInsights = HealthRules.evaluateHealth(userContext)
            val agendaInsights = CalendarRules.evaluateAgenda(userContext)
            val generalRules = processRules(userContext)
            
            val allInsights = habitsInsights + tasksInsights + projectsInsights + healthInsights + agendaInsights + generalRules
            return InsightSorter.sort(allInsights)
        }
    }
}
