package com.example.core.ai.life_ai

import android.content.Context
import com.example.core.ai.memory.BehaviorAnalyzer
import com.example.core.ai.memory.SharedPreferencesMemoryRepository
import com.example.features.dashboard.domain.model.DashboardData

class PredictionEngine(context: Context) {
    private val repository = SharedPreferencesMemoryRepository(context)
    private val analyzer = BehaviorAnalyzer(repository)

    fun predictFocusPeriod(dashboardData: DashboardData): String {
        val report = analyzer.analyzeUserBehavior(dashboardData)
        return report.focusPeriodSuggestion
    }

    fun getPerformancePrediction(dashboardData: DashboardData): Float {
        val report = analyzer.analyzeUserBehavior(dashboardData)
        return report.score
    }

    fun getWarnings(dashboardData: DashboardData): List<String> {
        val report = analyzer.analyzeUserBehavior(dashboardData)
        return report.warningInsights
    }

    fun getStrengthsAndWeaknesses(dashboardData: DashboardData): Pair<String, String> {
        val report = analyzer.analyzeUserBehavior(dashboardData)
        return Pair(report.primaryStrength, report.primaryWeakness)
    }
}
