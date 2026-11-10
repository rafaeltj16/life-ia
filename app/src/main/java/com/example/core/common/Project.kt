package com.example.core.common

import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: String,
    val userId: String? = null,
    val completed: Boolean = false
)
