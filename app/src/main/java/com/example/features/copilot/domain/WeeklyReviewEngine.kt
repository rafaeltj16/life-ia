package com.example.features.copilot.domain

import com.example.features.copilot.data.CopilotRepository
import com.example.features.copilot.data.WeeklyReview
import kotlinx.coroutines.flow.Flow

class WeeklyReviewEngine(
    private val repository: CopilotRepository
) {
    fun getWeeklyReviews(): Flow<List<WeeklyReview>> = repository.getWeeklyReviews()
}
