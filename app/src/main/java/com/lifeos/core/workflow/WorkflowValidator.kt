package com.lifeos.core.workflow

sealed class WorkflowValidationResult {
    object Valid : WorkflowValidationResult()
    data class Invalid(val errors: List<String>) : WorkflowValidationResult() {
        val errorMessage: String get() = errors.joinToString("; ")
    }
}

class WorkflowValidator {

    fun validate(workflow: Workflow): WorkflowValidationResult {
        val errors = mutableListOf<String>()

        if (workflow.name.isBlank()) {
            errors.add("Nome do workflow não pode ser vazio")
        }

        if (workflow.actions.isEmpty()) {
            errors.add("Workflow deve conter ao menos uma ação")
        }

        return if (errors.isEmpty()) {
            WorkflowValidationResult.Valid
        } else {
            WorkflowValidationResult.Invalid(errors)
        }
    }
}
