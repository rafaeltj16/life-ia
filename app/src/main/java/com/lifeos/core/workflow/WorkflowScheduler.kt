package com.lifeos.core.workflow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class ScheduledWorkflowItem(
    val workflowId: String,
    val workflowName: String,
    val nextRunFormatted: String,
    val isRecurring: Boolean = true
)

class WorkflowScheduler(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _scheduledWorkflows = mutableListOf<ScheduledWorkflowItem>()
    private val _triggerEvents = MutableSharedFlow<String>()
    val triggerEvents: SharedFlow<String> = _triggerEvents.asSharedFlow()

    private var activeSchedulerJob: Job? = null

    init {
        startScheduler()
    }

    private fun startScheduler() {
        activeSchedulerJob?.cancel()
        activeSchedulerJob = scope.launch {
            while (true) {
                delay(60000) // Check periodically every minute
            }
        }
    }

    fun scheduleWorkflow(workflow: Workflow) {
        if (!workflow.isEnabled) return

        when (val trigger = workflow.trigger) {
            is Trigger.Schedule -> {
                _scheduledWorkflows.removeAll { it.workflowId == workflow.id }
                _scheduledWorkflows.add(
                    ScheduledWorkflowItem(
                        workflowId = workflow.id,
                        workflowName = workflow.name,
                        nextRunFormatted = trigger.cronOrTime
                    )
                )
            }
            is Trigger.Time -> {
                _scheduledWorkflows.removeAll { it.workflowId == workflow.id }
                val timeStr = String.format("%02d:%02d", trigger.hour, trigger.minute)
                _scheduledWorkflows.add(
                    ScheduledWorkflowItem(
                        workflowId = workflow.id,
                        workflowName = workflow.name,
                        nextRunFormatted = timeStr
                    )
                )
            }
            else -> {
                // Event or manual triggers do not require time scheduling
            }
        }
    }

    fun unscheduleWorkflow(workflowId: String) {
        _scheduledWorkflows.removeAll { it.workflowId == workflowId }
    }

    fun getScheduledItems(): List<ScheduledWorkflowItem> = _scheduledWorkflows.toList()

    suspend fun emitTriggerEvent(eventName: String) {
        _triggerEvents.emit(eventName)
    }
}
