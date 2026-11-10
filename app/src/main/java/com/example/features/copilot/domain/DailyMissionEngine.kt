package com.example.features.copilot.domain

import com.example.features.copilot.data.CopilotRepository
import com.example.features.copilot.data.DailyMission
import com.example.services.gemini.GeminiRepository
import com.example.services.gemini.GeminiUserContext
import com.example.di.GeminiModule
import kotlinx.coroutines.flow.Flow

class DailyMissionEngine(
    private val repository: CopilotRepository
) {
    fun getMissions(): Flow<List<DailyMission>> = repository.getDailyMissions()

    suspend fun toggleMission(missionId: String) {
        repository.completeMission(missionId)
    }

    /**
     * Communicates with the Gemini API via GeminiRepository to generate and update daily missions.
     */
    suspend fun generateMissionsWithGemini(
        context: GeminiUserContext,
        geminiRepository: GeminiRepository = GeminiModule.geminiRepository
    ): List<DailyMission> {
        val geminiMissions = geminiRepository.generateDailyMissions(context)
        val copilotMissions = geminiMissions.map { it.toCopilotMission() }
        repository.updateDailyMissions(copilotMissions)
        return copilotMissions
    }
}
