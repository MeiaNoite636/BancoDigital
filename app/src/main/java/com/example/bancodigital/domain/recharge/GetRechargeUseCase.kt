package com.example.bancodigital.domain.recharge

import com.example.bancodigital.data.model.Recharge
import com.example.bancodigital.data.repository.recharge.RechargeDataSourceImpl
import javax.inject.Inject

class GetRechargeUseCase @Inject constructor(
    private val rechargeDataSourceImpl: RechargeDataSourceImpl
) {
    suspend operator fun invoke(id: String) : Recharge{
        return rechargeDataSourceImpl.getRecharge(id)
    }
}