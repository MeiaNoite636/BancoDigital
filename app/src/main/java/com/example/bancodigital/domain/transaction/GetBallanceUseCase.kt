package com.example.bancodigital.domain.transaction

import com.example.bancodigital.data.enum.TransactionType
import com.example.bancodigital.data.model.Transaction
import com.example.bancodigital.data.repository.transaction.TransactionDataSourceImpl
import javax.inject.Inject

class GetBallanceUseCase @Inject constructor(
    private val transactionDataSourceImpl: TransactionDataSourceImpl
) {

    suspend operator fun invoke() : Float {
        var cashIn = 0f
        var cashOut = 0f

        transactionDataSourceImpl.getTransactions().forEach(){transaction ->
            if (transaction.type == TransactionType.CASH_IN){
                cashIn += transaction.amount
            }else{
                cashOut += transaction.amount
            }
        }

        return cashIn - cashOut
    }
}