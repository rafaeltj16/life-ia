package com.lifeos.core_ai.learning

class AdaptiveLearningEngine(
    private val repository: LearningRepository
) {

    suspend fun register(
        event: LearningEvent
    ) {
        repository.save(event)
        repository.updateProfile()
    }

    suspend fun profile(): UserBehaviorProfile {
        return repository.loadProfile()
    }

}
