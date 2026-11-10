package com.lifeos.tasks.domain.model

typealias Task = com.example.core.common.Task

val Task.isOverdue: Boolean
    get() = !isCompleted && dueDate == "Atrasadas"
