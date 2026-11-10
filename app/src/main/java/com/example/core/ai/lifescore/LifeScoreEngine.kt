package com.example.core.ai.lifescore

import com.example.core.ai.model.DashboardData

class LifeScoreEngine(
    private val repository: LifeScoreRepository = LifeScoreRepository()
) {

    fun calculateScore(
        data: DashboardData,
        weights: LifeScoreWeights = LifeScoreWeights.DEFAULT
    ): LifeScoreResult {
        return calculateScore(
            completedTasks = maxOf(0, 10 - data.pendingTasks - data.overdueTasks),
            totalTasks = maxOf(1, 10 - data.pendingTasks + data.overdueTasks),
            overdueTasks = data.overdueTasks,
            completedHabits = data.completedHabitsToday,
            totalHabits = data.totalHabitsToday,
            streakDays = data.currentStreak,
            activeProjects = maxOf(0, 5 - data.inactiveProjects),
            totalProjects = 5,
            healthScoreInput = data.waterPercentage,
            financeScoreInput = 80,
            weights = weights
        )
    }

    fun calculateScore(
        completedTasks: Int,
        totalTasks: Int,
        overdueTasks: Int = 0,
        completedHabits: Int,
        totalHabits: Int,
        streakDays: Int = 0,
        activeProjects: Int = 1,
        totalProjects: Int = 1,
        healthScoreInput: Int = 75,
        financeScoreInput: Int = 75,
        weights: LifeScoreWeights = LifeScoreWeights.DEFAULT
    ): LifeScoreResult {
        val normWeights = weights.normalizedWeights()

        // 1. Task Score (0..100)
        val taskCompletionRatio = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0.8f
        val overduePenalty = overdueTasks * 8
        val taskScore = ((taskCompletionRatio * 100) - overduePenalty).toInt().coerceIn(0, 100)
        val taskDetails = if (overdueTasks > 0) "$completedTasks/$totalTasks concluídas ($overdueTasks em atraso)" else "$completedTasks/$totalTasks tarefas concluídas"

        // 2. Habit Score (0..100)
        val habitCompletionRatio = if (totalHabits > 0) completedHabits.toFloat() / totalHabits.toFloat() else 0.75f
        val habitBaseScore = (habitCompletionRatio * 100).toInt()
        val streakBonus = (streakDays * 2).coerceAtMost(15)
        val habitScore = (habitBaseScore + streakBonus).coerceIn(0, 100)
        val habitDetails = "$completedHabits/$totalHabits hábitos hoje • Ofensiva de $streakDays dias"

        // 3. Project Score (0..100)
        val projectRatio = if (totalProjects > 0) activeProjects.toFloat() / totalProjects.toFloat() else 0.7f
        val projectScore = (projectRatio * 100).toInt().coerceIn(0, 100)
        val projectDetails = "$activeProjects de $totalProjects projetos com progresso ativo"

        // 4. Finance Score (0..100)
        val financeScore = financeScoreInput.coerceIn(0, 100)
        val financeDetails = "Orçamento sob controle ($financeScore/100)"

        // 5. Health Score (0..100)
        val healthScore = healthScoreInput.coerceIn(0, 100)
        val healthDetails = "Hidratação e rotina saudável ($healthScore/100)"

        val categories = listOf(
            CategoryScore(ScoreCategory.PRODUCTIVITY, taskScore, 100, taskDetails),
            CategoryScore(ScoreCategory.HABITS, habitScore, 100, habitDetails),
            CategoryScore(ScoreCategory.PROJECTS, projectScore, 100, projectDetails),
            CategoryScore(ScoreCategory.FINANCE, financeScore, 100, financeDetails),
            CategoryScore(ScoreCategory.HEALTH, healthScore, 100, healthDetails)
        )

        // Weighted Average
        val weightedSum = (taskScore * normWeights.taskWeight) +
                (habitScore * normWeights.habitWeight) +
                (projectScore * normWeights.projectWeight) +
                (financeScore * normWeights.financeWeight) +
                (healthScore * normWeights.healthWeight)

        val finalScore = weightedSum.toInt().coerceIn(0, 100)

        val insights = generateInsights(taskScore, habitScore, projectScore, overdueTasks, streakDays)
        val recommendations = generateRecommendations(taskScore, habitScore, financeScore, overdueTasks)

        val result = LifeScoreResult(
            overallScore = finalScore,
            categoryScores = categories,
            weights = weights,
            streakBonus = streakBonus,
            insights = insights,
            recommendations = recommendations
        )

        repository.updateCurrentScore(result)

        return result
    }

    private fun generateInsights(
        taskScore: Int,
        habitScore: Int,
        projectScore: Int,
        overdueTasks: Int,
        streakDays: Int
    ): List<String> {
        val list = mutableListOf<String>()

        if (streakDays >= 7) {
            list.add("Excelente consistência! Sequência ativa de $streakDays dias mantida.")
        }
        if (overdueTasks > 0) {
            list.add("Atenção: $overdueTasks tarefa(s) em atraso estão reduzindo a pontuação de produtividade.")
        }
        if (habitScore >= 80) {
            list.add("Sua rotina de hábitos diários está em ritmo de alta performance.")
        }
        if (taskScore >= 85) {
            list.add("Execução de tarefas impecável no período recente.")
        }
        if (list.isEmpty()) {
            list.add("Desempenho estável. Pequenos ajustes diários trarão grande evolução.")
        }

        return list
    }

    private fun generateRecommendations(
        taskScore: Int,
        habitScore: Int,
        financeScore: Int,
        overdueTasks: Int
    ): List<String> {
        val recs = mutableListOf<String>()

        if (overdueTasks > 0) {
            recs.add("Resolva ou replaneje as $overdueTasks tarefas em atraso para recuperar pontos.")
        }
        if (habitScore < 70) {
            recs.add("Foque em concluir ao menos 2 hábitos essenciais no início da manhã.")
        }
        if (taskScore < 70) {
            recs.add("Divida tarefas grandes em sub-tarefas menores de 15 minutos.")
        }
        if (financeScore < 70) {
            recs.add("Revise seus gastos recentes e defina um limite semanal de despesas.")
        }
        if (recs.isEmpty()) {
            recs.add("Mantenha o excelente ritmo! Desafie-se adicionando uma nova meta semanal.")
        }

        return recs
    }
}
