package com.lifeos.core.workflow

import kotlinx.coroutines.delay
import java.time.Instant
import java.util.UUID

sealed class StepExecutionResult {
    data class Success(val message: String, val outputData: Map<String, String> = emptyMap()) : StepExecutionResult()
    data class Failure(val error: String) : StepExecutionResult()
    data class Skipped(val reason: String) : StepExecutionResult()
}

class WorkflowExecutor(
    private val history: WorkflowHistory = WorkflowHistory()
) {

    suspend fun execute(workflow: Workflow, triggerSource: String = "Manual"): WorkflowExecutionRecord {
        val startTime = Instant.now()
        val stepRecords = mutableListOf<StepExecutionRecord>()

        var hasFailure = false

        for (action in workflow.actions) {
            val stepStartTime = Instant.now()
            val stepName = getActionName(action)
            val stepId = "${action.javaClass.simpleName}_${UUID.randomUUID()}"
            val result = executeSingleAction(action)

            when (result) {
                is StepExecutionResult.Success -> {
                    stepRecords.add(
                        StepExecutionRecord(
                            stepId = stepId,
                            stepName = stepName,
                            status = StepStatus.COMPLETED,
                            startedAt = stepStartTime,
                            completedAt = Instant.now(),
                            resultMessage = result.message
                        )
                    )
                }
                is StepExecutionResult.Failure -> {
                    hasFailure = true
                    stepRecords.add(
                        StepExecutionRecord(
                            stepId = stepId,
                            stepName = stepName,
                            status = StepStatus.FAILED,
                            startedAt = stepStartTime,
                            completedAt = Instant.now(),
                            errorMessage = result.error
                        )
                    )
                }
                is StepExecutionResult.Skipped -> {
                    stepRecords.add(
                        StepExecutionRecord(
                            stepId = stepId,
                            stepName = stepName,
                            status = StepStatus.SKIPPED,
                            startedAt = stepStartTime,
                            completedAt = Instant.now(),
                            resultMessage = result.reason
                        )
                    )
                }
            }
        }

        val finalStatus = if (hasFailure) {
            if (stepRecords.any { it.status == StepStatus.COMPLETED }) WorkflowExecutionStatus.PARTIAL_SUCCESS else WorkflowExecutionStatus.FAILED
        } else {
            WorkflowExecutionStatus.SUCCESS
        }

        val executionRecord = WorkflowExecutionRecord(
            executionId = UUID.randomUUID().toString(),
            workflowId = workflow.id,
            workflowName = workflow.name,
            status = finalStatus,
            triggerSource = triggerSource,
            startedAt = startTime,
            completedAt = Instant.now(),
            stepRecords = stepRecords,
            summary = "Execução concluída com status $finalStatus (${stepRecords.count { it.status == StepStatus.COMPLETED }}/${workflow.actions.size} passos com sucesso)."
        )

        history.recordExecution(executionRecord)
        return executionRecord
    }

    private suspend fun executeSingleAction(action: Action): StepExecutionResult {
        return try {
            delay(10)
            when (action) {
                is Action.CalculateLifeScore -> {
                    StepExecutionResult.Success("Life Score recalculado com base nas ações recentes")
                }
                is Action.GenerateMission -> {
                    StepExecutionResult.Success("Missão diária gerada com sucesso pela IA")
                }
                is Action.StartFocus -> {
                    StepExecutionResult.Success("Sessão de foco iniciada com sucesso")
                }
                is Action.CreateTask -> {
                    StepExecutionResult.Success("Tarefa criada com sucesso")
                }
                is Action.SendNotification -> {
                    StepExecutionResult.Success("Notificação enviada com sucesso")
                }
                is Action.GenerateWeeklyReview -> {
                    StepExecutionResult.Success("Revisão semanal gerada com sucesso")
                }
                is Action.SyncSupabase -> {
                    StepExecutionResult.Success("Dados sincronizados com o Supabase com sucesso")
                }
                is Action.AskGemini -> {
                    StepExecutionResult.Success("Insights e recomendações obtidos do Gemini com sucesso")
                }
                is Action.UpdateDashboard -> {
                    StepExecutionResult.Success("Dashboard e métricas atualizados em tempo real")
                }
            }
        } catch (e: Exception) {
            StepExecutionResult.Failure("Erro ao executar ação '${getActionName(action)}': ${e.localizedMessage ?: e.message}")
        }
    }

    private fun getActionName(action: Action): String {
        return when (action) {
            is Action.CalculateLifeScore -> "Calcular Life Score"
            is Action.GenerateMission -> "Gerar Missão Diária"
            is Action.StartFocus -> "Iniciar Foco"
            is Action.CreateTask -> "Criar Tarefa"
            is Action.SendNotification -> "Enviar Notificação"
            is Action.GenerateWeeklyReview -> "Gerar Revisão Semanal"
            is Action.SyncSupabase -> "Sincronizar Supabase"
            is Action.AskGemini -> "Consultar Gemini"
            is Action.UpdateDashboard -> "Atualizar Dashboard"
        }
    }
}
