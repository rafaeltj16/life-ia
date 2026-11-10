package com.lifeos.core_ai.provider

import com.lifeos.calendar.domain.model.CalendarEvent
import java.time.LocalDate

interface CalendarProvider {

    suspend fun getEvents(
        date: LocalDate
    ): List<CalendarEvent>

}
