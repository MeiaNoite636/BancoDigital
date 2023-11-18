package com.example.bancodigital.domain.transaction

import com.example.bancodigital.data.model.Transaction
import com.example.bancodigital.data.repository.transaction.TransactionDataSourceImpl
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val transactionDataSourceImpl: TransactionDataSourceImpl
) {
    suspend operator fun invoke(transaction: Transaction) {
        transactionDataSourceImpl.saveTransaction(transaction)
    }
}