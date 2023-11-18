package com.example.bancodigital.di

import com.example.bancodigital.data.repository.auth.AuthFirebaseDataSource
import com.example.bancodigital.data.repository.auth.AuthFirebaseDataSourceImpl
import com.example.bancodigital.data.repository.deposit.DepositDataSource
import com.example.bancodigital.data.repository.deposit.DepositDataSourceImpl
import com.example.bancodigital.data.repository.recharge.RechargeDataSource
import com.example.bancodigital.data.repository.recharge.RechargeDataSourceImpl
import com.example.bancodigital.data.repository.transaction.TransactionDataSource
import com.example.bancodigital.data.repository.transaction.TransactionDataSourceImpl
import com.example.bancodigital.data.repository.transfer.TransferDataSource
import com.example.bancodigital.data.repository.transfer.TransferDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun bindsAuthDataSource(
        authFirebaseDataSourceImpl: AuthFirebaseDataSourceImpl
    ): AuthFirebaseDataSource

    @Binds
    abstract fun bindsDepositDataSource(
        DepositDataSourceImpl: DepositDataSourceImpl
    ): DepositDataSource

    @Binds
    abstract fun bindsTransactionDataSource(
        transactionDataSourceImpl: TransactionDataSourceImpl
    ): TransactionDataSource

    @Binds
    abstract fun bindsRechargeDataSource(
        rechargeDataSourceImpl: RechargeDataSourceImpl
    ): RechargeDataSource

    @Binds
    abstract fun bindsTransferDataSource(
        transferDataSourceImpl: TransferDataSourceImpl
    ): TransferDataSource
}