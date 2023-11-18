package com.example.bancodigital.data.model

import android.os.Parcelable
import com.google.firebase.database.FirebaseDatabase
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transfer(
    var id: String = "",
    var idUserReceived: String = "",
    var idUserSend: String = "",
    var amount: Float = 0f,
    var date: Long = 0
) : Parcelable {
    init {
        this.id = FirebaseDatabase.getInstance().reference.push().key ?: ""
    }
}
