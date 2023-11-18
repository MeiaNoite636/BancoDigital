package com.example.bancodigital.presenter.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.bancodigital.domain.auth.RegisterUseCase
import com.example.bancodigital.util.StateView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUsecase: RegisterUseCase
) : ViewModel() {

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ) = liveData(Dispatchers.IO) {
        try {
            emit(StateView.Loading())

            val user = registerUsecase.invoke(name, email, phone, password)

            emit(StateView.Sucess(user))

        }catch (ex: Exception) {

        }
    }
}