package com.lifeos.core_ai.context

class ContextEngine(
    private val provider: ContextProvider
) {

    suspend fun current(): UserContextSnapshot {
        return provider.loadContext()
    }

}
