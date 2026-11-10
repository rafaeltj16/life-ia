package com.example.core.ai.memory

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import java.util.UUID

interface MemoryRepository {
    fun getMemories(): List<UserMemory>
    fun getMemoriesFlow(): StateFlow<List<UserMemory>>
    fun saveMemory(memory: UserMemory)
    fun deleteMemory(id: String)
    fun clearAll()

    suspend fun saveEvent(event: UserEvent)
    suspend fun loadProfile(): MemoryProfile
    suspend fun updateProfile()
}

class SharedPreferencesMemoryRepository(private val context: Context) : MemoryRepository {
    private val sharedPrefs = context.getSharedPreferences("lifeai_memory_prefs", Context.MODE_PRIVATE)
    private val _memoriesFlow = MutableStateFlow<List<UserMemory>>(emptyList())
    private val eventsKey = "user_events_json"
    private val profileKey = "memory_profile_json"
    
    init {
        loadMemories()
    }
    
    override fun getMemories(): List<UserMemory> {
        return _memoriesFlow.value
    }
    
    override fun getMemoriesFlow(): StateFlow<List<UserMemory>> {
        return _memoriesFlow.asStateFlow()
    }
    
    override fun saveMemory(memory: UserMemory) {
        val current = _memoriesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == memory.id || (it.fact.lowercase() == memory.fact.lowercase() && it.category == memory.category) }
        if (index != -1) {
            val existing = current[index]
            current[index] = memory.copy(
                id = existing.id,
                strength = existing.strength + 1,
                lastUpdated = System.currentTimeMillis()
            )
        } else {
            current.add(memory)
        }
        _memoriesFlow.value = current
        saveToPrefs(current)
    }
    
    override fun deleteMemory(id: String) {
        val current = _memoriesFlow.value.filter { it.id != id }
        _memoriesFlow.value = current
        saveToPrefs(current)
    }
    
    override fun clearAll() {
        _memoriesFlow.value = emptyList()
        sharedPrefs.edit().remove("memories_json").remove(eventsKey).remove(profileKey).apply()
    }

    override suspend fun saveEvent(event: UserEvent) {
        val currentEvents = loadEvents()
        val updatedEvents = currentEvents + event
        saveEvents(updatedEvents)
        updateProfile()
    }

    override suspend fun loadProfile(): MemoryProfile {
        val jsonStr = sharedPrefs.getString(profileKey, null) ?: return MemoryProfile()
        return try {
            val obj = JSONObject(jsonStr)
            val bestFocusTime = obj.optString("bestFocusTime", null)?.let { if (it == "null" || it.isBlank()) null else LocalTime.parse(it) }
            val worstFocusTime = obj.optString("worstFocusTime", null)?.let { if (it == "null" || it.isBlank()) null else LocalTime.parse(it) }
            val mostProductiveDay = obj.optString("mostProductiveDay", null)?.let { if (it == "null" || it.isBlank()) null else DayOfWeek.valueOf(it) }
            val leastProductiveDay = obj.optString("leastProductiveDay", null)?.let { if (it == "null" || it.isBlank()) null else DayOfWeek.valueOf(it) }
            MemoryProfile(
                bestFocusTime = bestFocusTime,
                worstFocusTime = worstFocusTime,
                mostProductiveDay = mostProductiveDay,
                leastProductiveDay = leastProductiveDay,
                averageCompletedTasks = obj.optDouble("averageCompletedTasks", 0.0),
                averageCompletedHabits = obj.optDouble("averageCompletedHabits", 0.0),
                currentLifeScore = obj.optInt("currentLifeScore", 0),
                currentMomentum = obj.optInt("currentMomentum", 0)
            )
        } catch (e: Exception) {
            MemoryProfile()
        }
    }

    override suspend fun updateProfile() {
        val events = loadEvents()
        if (events.isEmpty()) return
        
        val focusSessions = events.filterIsInstance<UserEvent.FocusSessionFinished>()
        val bestFocusTime = focusSessions.maxByOrNull { it.minutes }?.finishedAt
            ?.atZone(java.time.ZoneId.systemDefault())?.toLocalTime()
        
        val worstFocusTime = focusSessions.minByOrNull { it.minutes }?.finishedAt
            ?.atZone(java.time.ZoneId.systemDefault())?.toLocalTime()

        val completionsByDay = events.filter { it is UserEvent.TaskCompleted || it is UserEvent.HabitCompleted }
            .groupBy { 
                val instant = when (it) {
                    is UserEvent.TaskCompleted -> it.completedAt
                    is UserEvent.HabitCompleted -> it.completedAt
                    else -> Instant.now()
                }
                instant.atZone(java.time.ZoneId.systemDefault()).dayOfWeek
            }
        
        val mostProductiveDay = completionsByDay.maxByOrNull { it.value.size }?.key
        val leastProductiveDay = completionsByDay.minByOrNull { it.value.size }?.key

        val taskCompletions = events.filterIsInstance<UserEvent.TaskCompleted>()
        val habitCompletions = events.filterIsInstance<UserEvent.HabitCompleted>()
        
        val uniqueDays = events.map { 
            val instant = when (it) {
                is UserEvent.TaskCompleted -> it.completedAt
                is UserEvent.HabitCompleted -> it.completedAt
                is UserEvent.ProjectUpdated -> it.updatedAt
                is UserEvent.FocusSessionFinished -> it.finishedAt
            }
            instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        }.distinct().size.coerceAtLeast(1)

        val averageCompletedTasks = taskCompletions.size.toDouble() / uniqueDays
        val averageCompletedHabits = habitCompletions.size.toDouble() / uniqueDays

        val currentLifeScore = (70 + (habitCompletions.size * 2)).coerceIn(0, 100)
        val currentMomentum = (50 + (taskCompletions.size * 5)).coerceIn(0, 100)

        val profile = MemoryProfile(
            bestFocusTime = bestFocusTime,
            worstFocusTime = worstFocusTime,
            mostProductiveDay = mostProductiveDay,
            leastProductiveDay = leastProductiveDay,
            averageCompletedTasks = averageCompletedTasks,
            averageCompletedHabits = averageCompletedHabits,
            currentLifeScore = currentLifeScore,
            currentMomentum = currentMomentum
        )

        saveProfileToPrefs(profile)
    }

    private fun loadEvents(): List<UserEvent> {
        val jsonStr = sharedPrefs.getString(eventsKey, null) ?: return emptyList()
        return try {
            val list = mutableListOf<UserEvent>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val type = obj.getString("type")
                val timestamp = Instant.parse(obj.getString("timestamp"))
                val event = when (type) {
                    "TaskCompleted" -> UserEvent.TaskCompleted(obj.getString("taskId"), timestamp)
                    "HabitCompleted" -> UserEvent.HabitCompleted(obj.getString("habitId"), timestamp)
                    "ProjectUpdated" -> UserEvent.ProjectUpdated(obj.getString("projectId"), timestamp)
                    "FocusSessionFinished" -> UserEvent.FocusSessionFinished(obj.getInt("minutes"), timestamp)
                    else -> null
                }
                if (event != null) {
                    list.add(event)
                }
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveEvents(events: List<UserEvent>) {
        try {
            val array = JSONArray()
            for (event in events) {
                val obj = JSONObject()
                when (event) {
                    is UserEvent.TaskCompleted -> {
                        obj.put("type", "TaskCompleted")
                        obj.put("taskId", event.taskId)
                        obj.put("timestamp", event.completedAt.toString())
                    }
                    is UserEvent.HabitCompleted -> {
                        obj.put("type", "HabitCompleted")
                        obj.put("habitId", event.habitId)
                        obj.put("timestamp", event.completedAt.toString())
                    }
                    is UserEvent.ProjectUpdated -> {
                        obj.put("type", "ProjectUpdated")
                        obj.put("projectId", event.projectId)
                        obj.put("timestamp", event.updatedAt.toString())
                    }
                    is UserEvent.FocusSessionFinished -> {
                        obj.put("type", "FocusSessionFinished")
                        obj.put("minutes", event.minutes)
                        obj.put("timestamp", event.finishedAt.toString())
                    }
                }
                array.put(obj)
            }
            sharedPrefs.edit().putString(eventsKey, array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveProfileToPrefs(profile: MemoryProfile) {
        try {
            val obj = JSONObject().apply {
                put("bestFocusTime", profile.bestFocusTime?.toString() ?: "null")
                put("worstFocusTime", profile.worstFocusTime?.toString() ?: "null")
                put("mostProductiveDay", profile.mostProductiveDay?.name ?: "null")
                put("leastProductiveDay", profile.leastProductiveDay?.name ?: "null")
                put("averageCompletedTasks", profile.averageCompletedTasks)
                put("averageCompletedHabits", profile.averageCompletedHabits)
                put("currentLifeScore", profile.currentLifeScore)
                put("currentMomentum", profile.currentMomentum)
            }
            sharedPrefs.edit().putString(profileKey, obj.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadMemories() {
        val jsonStr = sharedPrefs.getString("memories_json", null)
        if (jsonStr.isNullOrBlank()) {
            val defaults = getDefaultMemories()
            _memoriesFlow.value = defaults
            saveToPrefs(defaults)
            return
        }
        try {
            val list = mutableListOf<UserMemory>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    UserMemory(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        fact = obj.optString("fact", ""),
                        category = obj.optString("category", "Geral"),
                        strength = obj.optInt("strength", 1),
                        confidence = obj.optDouble("confidence", 1.0).toFloat(),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        lastUpdated = obj.optLong("lastUpdated", System.currentTimeMillis())
                    )
                )
            }
            _memoriesFlow.value = list
        } catch (e: Exception) {
            _memoriesFlow.value = getDefaultMemories()
        }
    }
    
    private fun saveToPrefs(list: List<UserMemory>) {
        try {
            val array = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("fact", item.fact)
                    put("category", item.category)
                    put("strength", item.strength)
                    put("confidence", item.confidence.toDouble())
                    put("createdAt", item.createdAt)
                    put("lastUpdated", item.lastUpdated)
                }
                array.put(obj)
            }
            sharedPrefs.edit().putString("memories_json", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun getDefaultMemories(): List<UserMemory> {
        return listOf(
            UserMemory(fact = "Prefiro fazer exercícios no final do dia", category = "Saúde", strength = 1, confidence = 0.9f),
            UserMemory(fact = "Trabalho como desenvolvedor freelancer", category = "Trabalho", strength = 1, confidence = 1.0f),
            UserMemory(fact = "Minha principal meta financeira é investir R$ 1.000 por mês", category = "Financeiro", strength = 2, confidence = 0.85f),
            UserMemory(fact = "Quero ler pelo menos 15 páginas de livros antes de dormir", category = "Hábito", strength = 1, confidence = 0.95f)
        )
    }
}
