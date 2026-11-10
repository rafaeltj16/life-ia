package com.lifeos.core_ai.context

interface ContextProvider {
    suspend fun loadContext(): UserContextSnapshot
}
