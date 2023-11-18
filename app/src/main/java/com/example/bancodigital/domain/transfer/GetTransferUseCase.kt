package com.example.bancodigital.domain.transfer

import com.example.bancodigital.data.model.Transfer
import com.example.bancodigital.data.repository.transfer.TransferDataSourceImpl
import javax.inject.Inject

class GetTransferUseCase @Inject constructor(
    private val transferDataSourceImpl: TransferDataSourceImpl
) {
    suspend operator fun invoke(id:String): Transfer{
        return transferDataSourceImpl.getTransfer(id)
    }
}