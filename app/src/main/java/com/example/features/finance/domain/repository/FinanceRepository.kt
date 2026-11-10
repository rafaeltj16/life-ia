package com.example.features.finance.domain.repository

import com.example.core.common.Transaction

interface FinanceRepository {
    suspend fun getTransactions(userId: String): List<Transaction>
    suspend fun saveTransaction(userId: String, transaction: Transaction)
    suspend fun deleteTransaction(userId: String, transactionId: String)
}
