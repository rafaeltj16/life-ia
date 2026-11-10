package com.lifeos.core.ai.lifescore

interface LifeScoreRepository {

    suspend fun save(result: LifeScoreResult)

    suspend fun history(): List<ScoreHistory>

}
