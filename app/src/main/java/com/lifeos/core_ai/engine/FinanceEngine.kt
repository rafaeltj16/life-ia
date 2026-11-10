package com.lifeos.core_ai.engine

class FinanceEngine {
    fun calculateBudgetStatus(balance: Double, target: Double): String {
        return if (balance >= target) "Dentro do orçamento" else "Atenção ao limite"
    }
}
