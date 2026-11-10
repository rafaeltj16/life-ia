package com.lifeos.core.workflow

import java.time.Instant
import java.util.UUID

enum class WorkflowExecutionStatus {
    SUCCESS,
    PARTIAL_SUCCESS,
    FAILED,
    CANCELLED
}

data class StepExecutionRecord(
    val stepId: String,
    val stepName: String,
    val status: StepStatus,
    val startedAt: Instant,
    val completedAt: Instant? = null,
    val resultMessage: String? = null,
    val errorMessage: String? = null
)

data class WorkflowExecutionRecord(
    val executionId: String = UUID.randomUUID().toString(),
    val workflowId: String,
    val workflowName: String,
    val status: WorkflowExecutionStatus,
    val triggerSource: String = "Manual",
    val startedAt: Instant = Instant.now(),
    val completedAt: Instant? = null,
    val stepRecords: List<StepExecutionRecord> = emptyList(),
    val summary: String = ""
)

class WorkflowHistory {
    private val historyRecords = mutableListOf<WorkflowExecutionRecord>()

    fun recordExecution(record: WorkflowExecutionRecord) {
        historyRecords.add(0, record)
        if (historyRecords.size > 200) {
            historyRecords.removeAt(historyRecords.lastIndex)
        }
    }

    fun getRecords(workflowId: String? = null): List<WorkflowExecutionRecord> {
        return if (workflowId == null) {
            historyRecords.toList()
        } else {
            historyRecords.filter { it.workflowId == workflowId }
        }
    }

    fun clearHistory() {
        historyRecords.clear()
    }
}
