package com.example.features.finance.data.repository

import com.example.core.common.Transaction
import com.example.features.finance.domain.repository.FinanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FinanceRepositoryImpl : FinanceRepository {
    private val localTransactions = mutableMapOf<String, MutableList<Transaction>>()

    private fun getDefaultTransactions(): MutableList<Transaction> {
        return mutableListOf(
            Transaction(title = "Salário mensal SaaS", amount = 8500.00, isIncome = true, category = "Salário", date = "01 Jul"),
            Transaction(title = "Supermercado CompreBem", amount = 350.20, isIncome = false, category = "Alimentação", date = "01 Jul"),
            Transaction(title = "Assinatura Notion Pro", amount = 48.00, isIncome = false, category = "Assinaturas", date = "29 Jun"),
            Transaction(title = "Venda freelancer App", amount = 2400.00, isIncome = true, category = "Freelance", date = "28 Jun")
        )
    }

    override suspend fun getTransactions(userId: String): List<Transaction> = withContext(Dispatchers.IO) {
        return@withContext localTransactions.getOrPut(userId) { getDefaultTransactions() }
    }

    override suspend fun saveTransaction(userId: String, transaction: Transaction) {
        withContext(Dispatchers.IO) {
            val list = localTransactions.getOrPut(userId) { getDefaultTransactions() }
            val index = list.indexOfFirst { it.id == transaction.id }
            if (index != -1) {
                list[index] = transaction
            } else {
                list.add(transaction)
            }
        }
    }

    override suspend fun deleteTransaction(userId: String, transactionId: String) = withContext(Dispatchers.IO) {
        val list = localTransactions[userId]
        list?.removeAll { it.id == transactionId }
        Unit
    }
}
