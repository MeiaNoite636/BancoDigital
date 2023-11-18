package com.example.bancodigital.data.model

import com.example.bancodigital.data.enum.TransactionOperation
import com.example.bancodigital.data.enum.TransactionType


data class Transaction(
    var id: String = "",
    val operation: TransactionOperation? = null,
    val date: Long = 0L,
    val amount: Float = 0f,
    var type: TransactionType? = null
)

