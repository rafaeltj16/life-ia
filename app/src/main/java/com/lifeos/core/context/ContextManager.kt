package com.lifeos.core.context

import kotlinx.coroutines.flow.Flow

class ContextManager(
    private val repository: ContextRepository = ContextRepositoryImpl(),
    private val collector: ContextCollector = ContextCollector(),
    private val analyzer: ContextAnalyzer = ContextAnalyzer(),
    private val ranker: MemoryRanker = MemoryRanker()
) {
    fun observeContext(): Flow<ContextSnapshot> = repository.getLatestSnapshot()

    suspend fun refreshContext(): ContextSnapshot {
        val snapshot = collector.collectCurrentSnapshot()
        repository.saveSnapshot(snapshot)
        return snapshot
    }

    fun analyzeCurrentContext(snapshot: ContextSnapshot): ContextAnalysisResult {
        return analyzer.analyzeSnapshot(snapshot)
    }

    fun rankConversationMemories(memories: List<ConversationMemory>, query: String): List<ConversationMemory> {
        return ranker.rankMemories(memories, query)
    }
}
