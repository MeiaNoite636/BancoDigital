package com.example.bancodigital.data.repository.recharge

import com.example.bancodigital.data.model.Recharge


interface RechargeDataSource {

    suspend fun saveRecharge(recharge: Recharge) : Recharge

    suspend fun getRecharge(id: String) : Recharge
}