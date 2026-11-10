package com.example.core.common

import java.util.UUID

// Navigation Modules
enum class NavigationModule(val title: String) {
    DASHBOARD("🏠 Dashboard AI"),
    LIFE_AI("🤖 LifeAI"),
    HABITS("✅ Hábitos"),
    TASKS("📋 Tarefas"),
    PROJECTS("🚀 Projetos"),
    AGENDA("📅 Agenda"),
    GOALS("🎯 Metas"),
    FINANCES("💰 Finanças"),
    HEALTH("❤️ Saúde"),
    PROFILE("👤 Perfil"),
    SETTINGS("⚙ Configurações")
}

// AI Chat Message
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
