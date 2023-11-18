package com.example.bancodigital.data.repository.deposit

import com.example.bancodigital.data.model.Deposit

interface DepositDataSource {

    suspend fun saveDeposit(deposit: Deposit) : Deposit

    suspend fun getDeposit(id: String) : Deposit
}