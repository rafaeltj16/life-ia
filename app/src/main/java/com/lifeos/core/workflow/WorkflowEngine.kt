package com.lifeos.core.workflow

import kotlinx.coroutines.flow.Flow

class WorkflowEngine(
    val repository: WorkflowRepository = InMemoryWorkflowRepository(),
    val validator: WorkflowValidator = WorkflowValidator(),
    val executor: WorkflowExecutor = WorkflowExecutor(),
    val scheduler: WorkflowScheduler = WorkflowScheduler(),
    val history: WorkflowHistory = WorkflowHistory(),
    val parser: WorkflowParser = WorkflowParser(),
    val intentDetector: WorkflowIntentDetector = WorkflowIntentDetector(),
    val lifeAI: LifeAI = LifeAI(
        intentDetector = intentDetector,
        parser = parser,
        validator = validator,
        executor = executor,
        repository = repository,
        history = history
    )
) {

    fun observeWorkflows(): Flow<List<Workflow>> = repository.observeWorkflows()

    fun getAllWorkflows(): List<Workflow> = repository.getWorkflows()

    fun getWorkflow(id: String): Workflow? = repository.getWorkflowById(id)

    /**
     * Executes the complete pipeline from user message:
     * Mensagem -> LifeAI -> Intent Detector -> Workflow Parser -> Workflow Builder -> Workflow Validator -> Workflow Executor
     */
    suspend fun processMessageWithLifeAI(message: String): LifeAIPipelineResult {
        return lifeAI.processMessage(message)
    }

    suspend fun processMessageWithLifeAI(mensagem: Mensagem): LifeAIPipelineResult {
        return lifeAI.processMessage(mensagem)
    }

    suspend fun executeWorkflow(workflowId: String, triggerSource: String = "Manual"): WorkflowExecutionRecord {
        val workflow = repository.getWorkflowById(workflowId)
            ?: throw IllegalArgumentException("Workflow com ID '$workflowId' não encontrado")

        val validation = validator.validate(workflow)
        if (validation is WorkflowValidationResult.Invalid) {
            val failedRecord = WorkflowExecutionRecord(
                workflowId = workflow.id,
                workflowName = workflow.name,
                status = WorkflowExecutionStatus.FAILED,
                triggerSource = triggerSource,
                summary = "Validação do workflow falhou: ${validation.errorMessage}"
            )
            history.recordExecution(failedRecord)
            return failedRecord
        }

        return executor.execute(workflow, triggerSource)
    }

    suspend fun executeDirect(workflow: Workflow, triggerSource: String = "Direct"): WorkflowExecutionRecord {
        val validation = validator.validate(workflow)
        if (validation is WorkflowValidationResult.Invalid) {
            val failedRecord = WorkflowExecutionRecord(
                workflowId = workflow.id,
                workflowName = workflow.name,
                status = WorkflowExecutionStatus.FAILED,
                triggerSource = triggerSource,
                summary = "Validação do workflow falhou: ${validation.errorMessage}"
            )
            history.recordExecution(failedRecord)
            return failedRecord
        }
        return executor.execute(workflow, triggerSource)
    }

    fun saveWorkflow(workflow: Workflow): WorkflowValidationResult {
        val validation = validator.validate(workflow)
        if (validation is WorkflowValidationResult.Valid) {
            repository.saveWorkflow(workflow)
            scheduler.scheduleWorkflow(workflow)
        }
        return validation
    }

    fun createFromTemplate(templateId: String, customParams: Map<String, String> = emptyMap()): Workflow? {
        val template = WorkflowTemplate.getPredefinedTemplates().find { it.templateId == templateId } ?: return null
        val workflow = parser.parseTemplate(template, customParams)
        repository.saveWorkflow(workflow)
        scheduler.scheduleWorkflow(workflow)
        return workflow
    }

    fun toggleWorkflow(id: String, isEnabled: Boolean) {
        repository.toggleWorkflow(id, isEnabled)
        val workflow = repository.getWorkflowById(id)
        if (workflow != null) {
            if (isEnabled) {
                scheduler.scheduleWorkflow(workflow)
            } else {
                scheduler.unscheduleWorkflow(id)
            }
        }
    }

    fun deleteWorkflow(id: String): Boolean {
        scheduler.unscheduleWorkflow(id)
        return repository.deleteWorkflow(id)
    }

    fun getExecutionHistory(workflowId: String? = null): List<WorkflowExecutionRecord> {
        return history.getRecords(workflowId)
    }

    fun getScheduledWorkflows(): List<ScheduledWorkflowItem> {
        return scheduler.getScheduledItems()
    }
}
