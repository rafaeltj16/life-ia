package com.lifeos.core_ai.usecase

import com.lifeos.core_ai.engine.SnapshotBuilder
import com.lifeos.core_ai.engine.CopilotEngine
import com.example.core.ai.model.CopilotDashboard

class CopilotUseCase(
    private val snapshotBuilder: SnapshotBuilder,
    private val copilotEngine: CopilotEngine
) {

    suspend operator fun invoke(): CopilotDashboard {
        val snapshot = snapshotBuilder.build()
        return copilotEngine.generate(snapshot)
    }

}
