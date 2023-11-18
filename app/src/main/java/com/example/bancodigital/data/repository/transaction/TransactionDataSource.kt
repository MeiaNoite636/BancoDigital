package com.example.bancodigital.data.repository.transaction

import com.example.bancodigital.data.model.Transaction

interface TransactionDataSource {

    suspend fun saveTransaction(transaction: Transaction)

    suspend fun getTransactions() : List<Transaction>
}