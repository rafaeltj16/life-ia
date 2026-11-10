package com.lifeos.core.context

class ContextProvider(
    private val repository: ContextRepository = ContextRepositoryImpl(),
    private val collector: ContextCollector = ContextCollector()
) {
    fun getCurrentContext(): ContextSnapshot {
        return collector.collectCurrentSnapshot()
    }
}
