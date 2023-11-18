package com.example.bancodigital.presenter.features.deposit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.bancodigital.data.model.Deposit
import com.example.bancodigital.data.model.Transaction
import com.example.bancodigital.domain.deposit.SaveDepositUseCase
import com.example.bancodigital.domain.transaction.SaveTransactionUseCase
import com.example.bancodigital.util.StateView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DepositViewModel @Inject constructor(
    private val saveDepositUseCase: SaveDepositUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase
) : ViewModel() {

    fun saveDeposit(deposit: Deposit) = liveData(Dispatchers.IO) {
        try {
            emit(StateView.Loading())

            val result = saveDepositUseCase.invoke(deposit)

            emit(StateView.Sucess(result))

        }catch (ex: Exception){
            emit(StateView.Error(ex.message))
        }
    }

    fun saveTransaction(transaction: Transaction) = liveData(Dispatchers.IO) {
        try {
            emit(StateView.Loading())

            saveTransactionUseCase.invoke(transaction)

            emit(StateView.Sucess(Unit))

        }catch (ex: Exception){
            emit(StateView.Error(ex.message))
        }
    }
}