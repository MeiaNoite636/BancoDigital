package com.example.bancodigital.presenter.features.extract

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.bancodigital.domain.transaction.GetTransactionUseCase
import com.example.bancodigital.util.StateView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ExtractViewModel @Inject constructor(
    private val getTransactionUseCase: GetTransactionUseCase
) : ViewModel() {

    fun getTransactions() = liveData(Dispatchers.IO) {
        try {
            emit(StateView.Loading())

            val transactions = getTransactionUseCase.invoke()

            emit(StateView.Sucess(transactions))

        } catch (ex: Exception) {
            emit(StateView.Error(ex.message))
        }
    }
}