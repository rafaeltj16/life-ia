package com.lifeos.core_ai.learning

interface LearningRepository {

    suspend fun save(event: LearningEvent)

    suspend fun loadProfile(): UserBehaviorProfile

    suspend fun updateProfile()

}
