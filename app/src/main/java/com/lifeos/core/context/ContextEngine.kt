package com.lifeos.core.context

class ContextEngine(
    val manager: ContextManager = ContextManager(),
    val provider: ContextProvider = ContextProvider(),
    val analyzer: ContextAnalyzer = ContextAnalyzer()
) {
    fun executeContextPipeline(): ContextAnalysisResult {
        val currentSnapshot = provider.getCurrentContext()
        return analyzer.analyzeSnapshot(currentSnapshot)
    }
}
