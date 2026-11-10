package com.lifeos.core.workflow

import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Represents the initial user input message in the LifeAI pipeline.
 * Mensagem -> LifeAI -> Intent Detector -> Workflow Parser -> Workflow Builder -> Workflow Validator -> Workflow Executor
 */
data class Mensagem(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val sender: String = "User",
    val timestamp: Instant = Instant.now(),
    val metadata: Map<String, String> = emptyMap()
) {
    companion object {
        fun from(text: String): Mensagem = Mensagem(content = text)
    }
}

enum class PipelineStage {
    MENSAGEM,
    LIFE_AI,
    INTENT_DETECTOR,
    WORKFLOW_PARSER,
    WORKFLOW_BUILDER,
    WORKFLOW_VALIDATOR,
    WORKFLOW_EXECUTOR
}

data class PipelineStageLog(
    val stage: PipelineStage,
    val stageName: String,
    val status: String,
    val description: String,
    val timestamp: Instant = Instant.now(),
    val durationMs: Long = 0
)

data class LifeAIPipelineResult(
    val mensagem: Mensagem,
    val detectedIntent: WorkflowIntent,
    val parsedSpec: ParsedWorkflowSpec,
    val builtWorkflow: Workflow,
    val validationResult: WorkflowValidationResult,
    val executionRecord: WorkflowExecutionRecord?,
    val stageLogs: List<PipelineStageLog>,
    val isSuccess: Boolean,
    val summary: String,
    val totalTimeMs: Long
)

/**
 * LifeAI: The core intelligence coordinator of LifeOS that executes the full pipeline:
 * Mensagem -> LifeAI -> Intent Detector -> Workflow Parser -> Workflow Builder -> Workflow Validator -> Workflow Executor
 */
class LifeAI(
    val intentDetector: WorkflowIntentDetector = WorkflowIntentDetector(),
    val parser: WorkflowParser = WorkflowParser(),
    val validator: WorkflowValidator = WorkflowValidator(),
    val executor: WorkflowExecutor = WorkflowExecutor(),
    val repository: WorkflowRepository = InMemoryWorkflowRepository(),
    val history: WorkflowHistory = WorkflowHistory()
) {
    private val _pipelineEvents = MutableSharedFlow<PipelineStageLog>()
    val pipelineEvents: Flow<PipelineStageLog> = _pipelineEvents.asSharedFlow()

    /**
     * Executes the complete 7-step LifeAI workflow pipeline from a raw text message.
     */
    suspend fun processMessage(text: String): LifeAIPipelineResult {
        return processMessage(Mensagem.from(text))
    }

    /**
     * Executes the complete 7-step LifeAI workflow pipeline:
     * 1. Mensagem
     * 2. LifeAI
     * 3. Intent Detector
     * 4. Workflow Parser
     * 5. Workflow Builder
     * 6. Workflow Validator
     * 7. Workflow Executor
     */
    suspend fun processMessage(mensagem: Mensagem): LifeAIPipelineResult {
        val startTotalTime = System.currentTimeMillis()
        val logs = mutableListOf<PipelineStageLog>()

        // 1. Mensagem received
        val log1 = PipelineStageLog(
            stage = PipelineStage.MENSAGEM,
            stageName = "Mensagem",
            status = "SUCCESS",
            description = "Mensagem recebida: '${mensagem.content}'"
        )
        logs.add(log1)
        _pipelineEvents.emit(log1)

        // 2. LifeAI Context Processing
        val t2 = System.currentTimeMillis()
        val log2 = PipelineStageLog(
            stage = PipelineStage.LIFE_AI,
            stageName = "LifeAI",
            status = "SUCCESS",
            description = "LifeAI contextualizou a mensagem e orquestrou o pipeline",
            durationMs = System.currentTimeMillis() - t2
        )
        logs.add(log2)
        _pipelineEvents.emit(log2)

        // 3. Intent Detector
        val t3 = System.currentTimeMillis()
        val detectedIntent = intentDetector.detect(mensagem.content)
        val log3 = PipelineStageLog(
            stage = PipelineStage.INTENT_DETECTOR,
            stageName = "Intent Detector",
            status = "SUCCESS",
            description = "Intenção identificada: '${detectedIntent.intentName}' (Confiança: ${(detectedIntent.confidence * 100).toInt()}%)",
            durationMs = System.currentTimeMillis() - t3
        )
        logs.add(log3)
        _pipelineEvents.emit(log3)

        // 4. Workflow Parser
        val t4 = System.currentTimeMillis()
        val parsedSpec = parser.parseIntent(detectedIntent, mensagem.content)
        val log4 = PipelineStageLog(
            stage = PipelineStage.WORKFLOW_PARSER,
            stageName = "Workflow Parser",
            status = "SUCCESS",
            description = "Especificação extraída com ${parsedSpec.actions.size} ações e gatilho ${parsedSpec.trigger.javaClass.simpleName}",
            durationMs = System.currentTimeMillis() - t4
        )
        logs.add(log4)
        _pipelineEvents.emit(log4)

        // 5. Workflow Builder
        val t5 = System.currentTimeMillis()
        val builtWorkflow = WorkflowBuilder.create()
            .name(parsedSpec.name)
            .description(parsedSpec.description)
            .category(parsedSpec.category)
            .trigger(parsedSpec.trigger)
            .addActions(parsedSpec.actions)
            .enabled(parsedSpec.enabled)
            .metadata(parsedSpec.metadata)
            .build()

        val log5 = PipelineStageLog(
            stage = PipelineStage.WORKFLOW_BUILDER,
            stageName = "Workflow Builder",
            status = "SUCCESS",
            description = "Workflow '${builtWorkflow.name}' construído com sucesso (ID: ${builtWorkflow.id.take(8)})",
            durationMs = System.currentTimeMillis() - t5
        )
        logs.add(log5)
        _pipelineEvents.emit(log5)

        // 6. Workflow Validator
        val t6 = System.currentTimeMillis()
        val validationResult = validator.validate(builtWorkflow)
        if (validationResult is WorkflowValidationResult.Invalid) {
            val log6Failed = PipelineStageLog(
                stage = PipelineStage.WORKFLOW_VALIDATOR,
                stageName = "Workflow Validator",
                status = "FAILED",
                description = "Validação falhou: ${validationResult.errorMessage}",
                durationMs = System.currentTimeMillis() - t6
            )
            logs.add(log6Failed)
            _pipelineEvents.emit(log6Failed)

            val failedRecord = WorkflowExecutionRecord(
                workflowId = builtWorkflow.id,
                workflowName = builtWorkflow.name,
                status = WorkflowExecutionStatus.FAILED,
                triggerSource = "LifeAI Pipeline",
                summary = "Validação rejeitada: ${validationResult.errorMessage}"
            )
            history.recordExecution(failedRecord)

            return LifeAIPipelineResult(
                mensagem = mensagem,
                detectedIntent = detectedIntent,
                parsedSpec = parsedSpec,
                builtWorkflow = builtWorkflow,
                validationResult = validationResult,
                executionRecord = failedRecord,
                stageLogs = logs,
                isSuccess = false,
                summary = "Pipeline interrompido na validação: ${validationResult.errorMessage}",
                totalTimeMs = System.currentTimeMillis() - startTotalTime
            )
        }

        val log6 = PipelineStageLog(
            stage = PipelineStage.WORKFLOW_VALIDATOR,
            stageName = "Workflow Validator",
            status = "SUCCESS",
            description = "Workflow validado e em conformidade estrutural",
            durationMs = System.currentTimeMillis() - t6
        )
        logs.add(log6)
        _pipelineEvents.emit(log6)

        // Save to repository
        repository.saveWorkflow(builtWorkflow)

        // 7. Workflow Executor
        val t7 = System.currentTimeMillis()
        val executionRecord = executor.execute(builtWorkflow, triggerSource = "LifeAI Mensagem")
        history.recordExecution(executionRecord)

        val executionSuccess = executionRecord.status == WorkflowExecutionStatus.SUCCESS
        val log7 = PipelineStageLog(
            stage = PipelineStage.WORKFLOW_EXECUTOR,
            stageName = "Workflow Executor",
            status = if (executionSuccess) "SUCCESS" else "FAILED",
            description = "Workflow executado com status ${executionRecord.status} (${executionRecord.stepRecords.size} ações processadas)",
            durationMs = System.currentTimeMillis() - t7
        )
        logs.add(log7)
        _pipelineEvents.emit(log7)

        val totalDuration = System.currentTimeMillis() - startTotalTime
        return LifeAIPipelineResult(
            mensagem = mensagem,
            detectedIntent = detectedIntent,
            parsedSpec = parsedSpec,
            builtWorkflow = builtWorkflow,
            validationResult = validationResult,
            executionRecord = executionRecord,
            stageLogs = logs,
            isSuccess = executionSuccess,
            summary = "Pipeline LifeAI concluído com sucesso: ${builtWorkflow.name} executado em ${totalDuration}ms",
            totalTimeMs = totalDuration
        )
    }
}
