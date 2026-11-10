package com.lifeos.core.workflow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

interface WorkflowRepository {
    fun observeWorkflows(): Flow<List<Workflow>>
    fun getWorkflows(): List<Workflow>
    fun getWorkflowById(id: String): Workflow?
    fun saveWorkflow(workflow: Workflow)
    fun deleteWorkflow(id: String): Boolean
    fun toggleWorkflow(id: String, isEnabled: Boolean)
}

class InMemoryWorkflowRepository : WorkflowRepository {
    private val _workflows = MutableStateFlow<List<Workflow>>(emptyList())

    init {
        // Initialize with default templates converted to active workflows
        val defaultWorkflows = WorkflowTemplate.getPredefinedTemplates().map { it.defaultWorkflow }
        _workflows.value = defaultWorkflows
    }

    override fun observeWorkflows(): Flow<List<Workflow>> = _workflows.asStateFlow()

    override fun getWorkflows(): List<Workflow> = _workflows.value

    override fun getWorkflowById(id: String): Workflow? {
        return _workflows.value.find { it.id == id }
    }

    override fun saveWorkflow(workflow: Workflow) {
        val currentList = _workflows.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == workflow.id }
        if (index != -1) {
            currentList[index] = workflow
        } else {
            currentList.add(0, workflow)
        }
        _workflows.value = currentList
    }

    override fun deleteWorkflow(id: String): Boolean {
        val currentList = _workflows.value.toMutableList()
        val removed = currentList.removeAll { it.id == id }
        if (removed) {
            _workflows.value = currentList
        }
        return removed
    }

    override fun toggleWorkflow(id: String, isEnabled: Boolean) {
        val currentList = _workflows.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(enabled = isEnabled)
            _workflows.value = currentList
        }
    }
}
