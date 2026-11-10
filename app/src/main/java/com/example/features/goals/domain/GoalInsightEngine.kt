package com.example.features.goals.domain

class GoalInsightEngine {

    fun generateInsight(goal: Goal): String {
        val calculator = GoalProgressCalculator()
        val percent = calculator.calculatePercentage(goal.currentProgress, goal.targetProgress)

        return when {
            percent >= 100 -> "Meta alcançada com sucesso! Parabéns pela dedicação."
            percent >= 75 -> "Você está na reta final ($percent%). Foco total para concluir!"
            percent >= 50 -> "Metade do caminho percorrido ($percent%). Mantenha a consistência."
            percent >= 25 -> "Bom progresso inicial ($percent%). Continue avançando dia a dia."
            else -> "Meta recém-iniciada ($percent%). Defina pequenos passos diários."
        }
    }
}
