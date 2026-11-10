package com.example.core.ai.prompt

import com.example.core.ai.repository.AIMemoryItem
import com.example.features.dashboard.domain.model.DashboardData

object PromptBuilder {

    fun buildDailyPlanPrompt(
        memories: List<AIMemoryItem>,
        habits: List<String>,
        tasks: List<String>
    ): String {
        val memoriesStr = memories.joinToString("; ") { it.fact }
        val habitsStr = habits.joinToString(", ")
        val tasksStr = tasks.joinToString(", ")

        return """
            Como assistente LifeOS AI, crie um plano diário de 5 horários estratégicos (manhã, início da tarde, tarde, fim do dia e noite) baseado nestas informações do usuário:
            Memórias/Preferências: $memoriesStr
            Hábitos ativos: $habitsStr
            Tarefas urgentes: $tasksStr

            Gere a resposta exatamente no seguinte formato estruturado de texto, com uma linha para cada atividade (exatamente 5 linhas):
            [Hora] | [Atividade] | [Categoria] | [Prioridade] | [Emoji]
            
            Exemplo de linha de retorno:
            08:00 | Meditação e Café | Saúde | Alta | 🧘
            
            Lembre-se de retornar APENAS as 5 linhas correspondentes às atividades, sem textos adicionais, introduções ou explicações.
        """.trimIndent()
    }

    fun buildRecommendationsPrompt(memories: List<AIMemoryItem>): String {
        val memoriesStr = memories.joinToString("; ") { it.fact }
        return """
            Com base nestas preferências do usuário: "$memoriesStr", sugira exatamente 4 recomendações personalizadas que ele possa adicionar ao LifeOS (um hábito, uma tarefa, uma dica financeira e um conselho de saúde).
            
            Retorne EXATAMENTE no seguinte formato de texto de 4 linhas, sem introdução ou texto adicional:
            [Hábito] | [Título] | [Descrição curta] | [Emoji]
            [Tarefa] | [Título] | [Descrição curta] | [Emoji]
            [Financeiro] | [Título] | [Descrição curta] | [Emoji]
            [Saúde] | [Título] | [Descrição curta] | [Emoji]

            Exemplo:
            Hábito | Caminhada de 20m | Caminhar após o almoço | 🚶
        """.trimIndent()
    }

    fun buildLifeAIEnginePrompt(
        dashboardData: DashboardData,
        memories: List<AIMemoryItem>
    ): String {
        val memoriesStr = memories.joinToString("; ") { it.fact }
        val completedTasks = dashboardData.todayTasks.count { it.isCompleted }
        val totalTasks = dashboardData.todayTasks.size
        val completedHabits = dashboardData.todayHabits.count { it.isCompletedToday }
        val totalHabits = dashboardData.todayHabits.size

        return """
            Você é o LifeAI Engine de alta performance. Analise os seguintes dados do Dashboard do usuário:
            - XP: ${dashboardData.xp}
            - Nível: ${dashboardData.level}
            - Streak: ${dashboardData.streak} dias
            - Hábitos de hoje: $completedHabits de $totalHabits concluídos
            - Tarefas de hoje: $completedTasks de $totalTasks concluídas
            - Finanças: Receita R${'$'} ${dashboardData.financeSummary.totalIncome} | Despesa R${'$'} ${dashboardData.financeSummary.totalExpense} | Saldo R${'$'} ${dashboardData.financeSummary.balance}
            
            Memórias e preferências persistentes do usuário:
            "$memoriesStr"
            
            Com base nesses dados, gere:
            1. Uma Análise de progresso geral curta (2 a 3 frases) contextualizada.
            2. Exatamente 3 Insights estratégicos (uma linha curta para cada insight).
            3. Exatamente 4 Recomendações personalizadas e instaláveis (um hábito, uma tarefa, uma financeira e uma de saúde), cada uma em uma linha formatada como:
               [Hábito/Tarefa/Financeiro/Saúde] | [Título] | [Descrição curta] | [Emoji]
            
            Sua resposta final deve ser estritamente formatada com os delimitadores exatos mostrados abaixo (não inclua nenhum outro texto fora deles):
            
            [ANALYSIS_START]
            (Sua análise aqui)
            [ANALYSIS_END]
            
            [INSIGHTS_START]
            (Insight 1)
            (Insight 2)
            (Insight 3)
            [INSIGHTS_END]
            
            [RECOMMENDATIONS_START]
            Hábito | [Título] | [Descrição] | [Emoji]
            Tarefa | [Título] | [Descrição] | [Emoji]
            Financeiro | [Título] | [Descrição] | [Emoji]
            Saúde | [Título] | [Descrição] | [Emoji]
            [RECOMMENDATIONS_END]
        """.trimIndent()
    }
}
