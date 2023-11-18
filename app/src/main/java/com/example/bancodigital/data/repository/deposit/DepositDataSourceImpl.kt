package com.example.bancodigital.data.repository.deposit

import com.example.bancodigital.data.model.Deposit
import com.example.bancodigital.util.FirebaseHelper
import com.google.firebase.database.*
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class DepositDataSourceImpl @Inject constructor(
    database: FirebaseDatabase
) : DepositDataSource {

    private val depositReference = database.reference
        .child("deposit")
        .child(FirebaseHelper.getUserId())

    override suspend fun saveDeposit(deposit: Deposit): Deposit {
        return suspendCoroutine { continuation ->
            depositReference
                .child(deposit.id)
                .setValue(deposit).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val dateReference = depositReference
                            .child(deposit.id)
                            .child("date")

                        dateReference.setValue(ServerValue.TIMESTAMP)
                            .addOnCompleteListener { taskUpdate ->
                                if (taskUpdate.isSuccessful) {
                                    continuation.resumeWith(Result.success(deposit))
                                } else {
                                    task.exception?.let { continuation.resumeWith(Result.failure(it)) }
                                }
                            }

                    } else {
                        task.exception?.let {
                            continuation.resumeWith(Result.failure(it))
                        }
                    }
                }
        }
    }

    override suspend fun getDeposit(id: String): Deposit {
        return suspendCoroutine { continuation ->
            depositReference
                .child(id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val deposit = snapshot.getValue(Deposit::class.java)

                        deposit?.let {
                            continuation.resumeWith(Result.success(it))
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().let {
                            continuation.resumeWith(Result.failure(it))
                        }
                    }

                })
        }
    }
}