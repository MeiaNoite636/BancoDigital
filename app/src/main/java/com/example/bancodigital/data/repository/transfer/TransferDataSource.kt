package com.example.bancodigital.data.repository.transfer

import com.example.bancodigital.data.model.Transfer

interface TransferDataSource {

    suspend fun saveTransfer(transfer: Transfer)

    suspend fun updateTransfer(transfer: Transfer)

    suspend fun getTransfer(id: String): Transfer

    suspend fun saveTransferTransaction(transfer: Transfer)

    suspend fun updateTransferTransaction(transfer: Transfer)
}