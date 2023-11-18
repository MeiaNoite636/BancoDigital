package com.example.bancodigital.presenter.features.transfer.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.bancodigital.domain.profile.GetProfileUseCase
import com.example.bancodigital.domain.transfer.GetTransferUseCase
import com.example.bancodigital.util.StateView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ReceiptTransferViewModel @Inject constructor(
    private val getTransferUseCase: GetTransferUseCase,
    private val getProfileUseCase: GetProfileUseCase
) :ViewModel(){

    fun getTransfer(id: String) = liveData(Dispatchers.IO){
        try {
            emit(StateView.Loading())

            val transfer = getTransferUseCase.invoke(id)

            emit(StateView.Sucess(transfer))
        }catch (ex: Exception){
            emit(StateView.Error(ex.message))
        }
    }

    fun getProfile(id: String)= liveData(Dispatchers.IO){
        try {
            emit(StateView.Loading())

            val profile = getProfileUseCase.invoke(id)

            emit(StateView.Sucess(profile))
        }catch (ex: Exception){
            emit(StateView.Error(ex.message))
        }
    }
}