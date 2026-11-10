package com.example.features.dashboard.domain.usecase

import com.example.core.network.GeminiServiceClient
import com.lifeos.features.tasks.domain.repository.TaskRepository
import com.lifeos.features.habits.domain.repository.HabitRepository

class GetAISuggestionUseCase(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(userId: String): String {
        val habits = habitRepository.getHabits(userId)
        val tasks = taskRepository.getTasks(userId)
        
        val prompt = """
            Com base no meu status de hábitos:
            ${habits.joinToString { it.name }}
            E nas tarefas concluídas. Sugira exatamente 3 tarefas ultra-focadas e práticas para o dia de hoje, organizadas de forma motivadora. Fale em português com no máximo 3 sentenças curtas.
        """.trimIndent()
        
        return GeminiServiceClient.generateResponse(prompt)
    }
}
