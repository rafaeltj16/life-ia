package com.example.core.common

import java.util.UUID

// Event Model (Agenda/Calendar)
data class AgendaEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val time: String,
    val category: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val impact: String = ""
) {
    val startTime: String get() = time
}

typealias CalendarEvent = AgendaEvent
