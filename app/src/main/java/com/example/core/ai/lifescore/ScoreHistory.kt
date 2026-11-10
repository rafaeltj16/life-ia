package com.example.core.ai.lifescore

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ScoreHistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val overallScore: Int,
    val taskScore: Int,
    val habitScore: Int,
    val projectScore: Int,
    val financeScore: Int,
    val healthScore: Int,
    val note: String = ""
) {
    val dateString: String
        get() = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timestamp))

    val fullDateString: String
        get() = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
}

data class ScoreHistory(
    val entries: List<ScoreHistoryEntry> = emptyList()
) {
    val latest: ScoreHistoryEntry? get() = entries.maxByOrNull { it.timestamp }

    val averageScore: Float
        get() = if (entries.isNotEmpty()) entries.map { it.overallScore }.average().toFloat() else 0f

    val trendDelta: Int
        get() {
            if (entries.size < 2) return 0
            val sorted = entries.sortedByDescending { it.timestamp }
            return sorted[0].overallScore - sorted[1].overallScore
        }

    val isImproving: Boolean get() = trendDelta > 0
}
