package com.lifeos.core.prediction

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface PredictionRepository {
    fun getProductivityModel(): Flow<ProductivityModel>
    fun getGoalForecasts(): Flow<List<GoalForecast>>
    suspend fun updateProductivityModel(model: ProductivityModel)
}

class PredictionRepositoryImpl : PredictionRepository {
    private val _productivityModel = MutableStateFlow(ProductivityModel())
    private val _goalForecasts = MutableStateFlow(
        listOf(
            GoalForecast("g1", "Lançar LifeOS AI", 30, 0.88f, 0, listOf("Mover 2 tarefas para quarta-feira")),
            GoalForecast("g2", "Dashboard Premium", 14, 0.65f, 2, listOf("Alocar blocos de foco dedicados no teclado"))
        )
    )

    override fun getProductivityModel(): Flow<ProductivityModel> = _productivityModel.asStateFlow()
    override fun getGoalForecasts(): Flow<List<GoalForecast>> = _goalForecasts.asStateFlow()

    override suspend fun updateProductivityModel(model: ProductivityModel) {
        _productivityModel.value = model
    }
}
