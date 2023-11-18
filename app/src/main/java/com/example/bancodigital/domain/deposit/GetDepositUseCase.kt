package com.example.bancodigital.domain.deposit

import com.example.bancodigital.data.model.Deposit
import com.example.bancodigital.data.repository.deposit.DepositDataSourceImpl
import javax.inject.Inject

class GetDepositUseCase @Inject constructor(private val depositDataSourceImpl: DepositDataSourceImpl) {

    suspend operator fun invoke(id: String) : Deposit{
        return depositDataSourceImpl.getDeposit(id)
    }
}