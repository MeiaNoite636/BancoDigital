package com.example.bancodigital.presenter.features.recharge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.bancodigital.domain.recharge.GetRechargeUseCase
import com.example.bancodigital.util.StateView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class RechargeReceiptViewModel @Inject constructor(
    private val getRechargeUseCase: GetRechargeUseCase
) : ViewModel(){

    fun getRecharge(idRecharge: String) = liveData(Dispatchers.IO){
        try {
            emit(StateView.Loading())

            val result = getRechargeUseCase.invoke(idRecharge)

            emit(StateView.Sucess(result))

        }catch (ex : Exception){
            emit(StateView.Error(ex.message))
        }
    }
}