package com.example.features.timeline.data

import com.example.features.timeline.domain.TimelineItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class TimelineRepositoryImpl : TimelineRepository {

    private val itemsState = MutableStateFlow<List<TimelineEntity>>(
        listOf(
            TimelineEntity(
                id = "1",
                title = "Academia",
                description = "Treino matinal de musculação e cárdio",
                timestampIso = LocalDateTime.now().withHour(8).withMinute(0).toString(),
                typeString = "CALENDAR",
                icon = "✓",
                scoreOrBadge = "08:00 ✓"
            ),
            TimelineEntity(
                id = "2",
                title = "Reunião",
                description = "Alinhamento de projeto com a equipe",
                timestampIso = LocalDateTime.now().withHour(9).withMinute(30).toString(),
                typeString = "CALENDAR",
                icon = "📅",
                scoreOrBadge = "09:30"
            ),
            TimelineEntity(
                id = "3",
                title = "Landing Page",
                description = "Design e implementação da landing page",
                timestampIso = LocalDateTime.now().withHour(10).withMinute(30).toString(),
                typeString = "TASK",
                icon = "💻",
                scoreOrBadge = "10:30 ★★★★★"
            ),
            TimelineEntity(
                id = "4",
                title = "Almoço",
                description = "Pausa para refeição e descanso",
                timestampIso = LocalDateTime.now().withHour(13).withMinute(0).toString(),
                typeString = "CALENDAR",
                icon = "🍱",
                scoreOrBadge = "13:00"
            ),
            TimelineEntity(
                id = "5",
                title = "Projeto",
                description = "Desenvolvimento e refatoração das views",
                timestampIso = LocalDateTime.now().withHour(15).withMinute(0).toString(),
                typeString = "TASK",
                icon = "🚀",
                scoreOrBadge = "15:00"
            ),
            TimelineEntity(
                id = "6",
                title = "Leitura",
                description = "30 minutos de leitura focada",
                timestampIso = LocalDateTime.now().withHour(17).withMinute(30).toString(),
                typeString = "HABIT",
                icon = "📚",
                scoreOrBadge = "17:30"
            )
        )
    )

    override fun getTimelineItems(): Flow<List<TimelineItem>> {
        return itemsState.asStateFlow().map { entities ->
            entities.map { TimelineMapper.toDomain(it) }
        }
    }

    override suspend fun addTimelineItem(item: TimelineItem) {
        val current = itemsState.value.toMutableList()
        current.add(0, TimelineMapper.toEntity(item))
        itemsState.value = current
    }
}

