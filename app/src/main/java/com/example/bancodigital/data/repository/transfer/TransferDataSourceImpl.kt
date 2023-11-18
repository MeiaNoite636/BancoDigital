package com.example.bancodigital.data.repository.transfer

import com.example.bancodigital.data.enum.TransactionOperation
import com.example.bancodigital.data.enum.TransactionType
import com.example.bancodigital.data.model.Transaction
import com.example.bancodigital.data.model.Transfer
import com.example.bancodigital.util.FirebaseHelper
import com.google.firebase.database.*
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class TransferDataSourceImpl @Inject constructor(
    database: FirebaseDatabase
) : TransferDataSource {

    private val transferReference = database.reference
        .child("transfer")

    private val transactionReference = database.reference
        .child("transaction")

    override suspend fun saveTransfer(transfer: Transfer) {
        suspendCoroutine { continuation ->
            transferReference
                .child(transfer.idUserSend)
                .child(transfer.id)
                .setValue(transfer).addOnCompleteListener { taskUserSent ->
                    if (taskUserSent.isSuccessful) {

                        transferReference
                            .child(transfer.idUserReceived)
                            .child(transfer.id)
                            .setValue(transfer).addOnCompleteListener { taskUserReceived ->
                                if (taskUserReceived.isSuccessful) {
                                    continuation.resumeWith(Result.success(Unit))
                                } else {
                                    taskUserReceived.exception?.let {
                                        continuation.resumeWith(Result.failure(it))
                                    }
                                }
                            }
                    } else {
                        taskUserSent.exception?.let {
                            continuation.resumeWith(Result.failure(it))
                        }
                    }
                }
        }
    }

    override suspend fun updateTransfer(transfer: Transfer) {
        return suspendCoroutine { continuation ->
            transferReference
                .child(transfer.idUserSend)
                .child(transfer.id)
                .child("date")
                .setValue(ServerValue.TIMESTAMP)
                .addOnCompleteListener { taskUpdateSend ->
                    if (taskUpdateSend.isSuccessful) {
                        transferReference
                            .child(transfer.idUserReceived)
                            .child(transfer.id)
                            .child("date")
                            .setValue(ServerValue.TIMESTAMP)
                            .addOnCompleteListener { taskUpdateReceived ->
                                if (taskUpdateReceived.isSuccessful) {
                                    continuation.resumeWith(Result.success(Unit))
                                } else {
                                    taskUpdateReceived.exception?.let {
                                        continuation.resumeWith(Result.failure(it))
                                    }
                                }
                            }
                    } else {
                        taskUpdateSend.exception?.let {
                            continuation.resumeWith(Result.failure(it))
                        }

                    }
                }
        }
    }

    override suspend fun getTransfer(id: String): Transfer {
        return suspendCoroutine { continuation ->
            transferReference
                .child(FirebaseHelper.getUserId())
                .child(id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val transfer = snapshot.getValue(Transfer::class.java)

                        transfer?.let {
                            continuation.resumeWith(Result.success(it))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWith(Result.failure(error.toException()))
                    }

                })
        }
    }

    override suspend fun saveTransferTransaction(transfer: Transfer) {
        return suspendCoroutine { continuation ->

            val transactionUserSend = Transaction(
                id = transfer.id,
                operation = TransactionOperation.TRANSFER,
                date = transfer.date,
                amount = transfer.amount,
                type = TransactionType.CASH_OUT
            )

            val transactionUserReceived = Transaction(
                id = transfer.id,
                operation = TransactionOperation.TRANSFER,
                date = transfer.date,
                amount = transfer.amount,
                type = TransactionType.CASH_IN
            )


            transactionReference
                .child(transfer.idUserSend)
                .child(transfer.id)
                .setValue(transactionUserSend)
                .addOnCompleteListener { taskUserSend ->
                    if (taskUserSend.isSuccessful) {

                        transactionReference
                            .child(transfer.idUserReceived)
                            .child(transfer.id)
                            .setValue(transactionUserReceived)
                            .addOnCompleteListener {taskUserReceived ->

                                if (taskUserReceived.isSuccessful){
                                    continuation.resumeWith(Result.success(Unit))
                                }else{
                                    taskUserReceived.exception?.let {
                                        continuation.resumeWith(Result.failure(it))
                                    }
                                }
                            }
                    } else {
                        taskUserSend.exception?.let {
                            continuation.resumeWith(Result.failure(it))
                        }
                    }
                }
        }
    }

    override suspend fun updateTransferTransaction(transfer: Transfer) {
        return suspendCoroutine { continuation ->
            transactionReference
                .child(transfer.idUserSend)
                .child(transfer.id)
                .child("date")
                .setValue(ServerValue.TIMESTAMP)
                .addOnCompleteListener { taskUpdateSend ->
                    if (taskUpdateSend.isSuccessful) {
                        transactionReference
                            .child(transfer.idUserReceived)
                            .child(transfer.id)
                            .child("date")
                            .setValue(ServerValue.TIMESTAMP)
                            .addOnCompleteListener { taskUpdateReceived ->
                                if (taskUpdateReceived.isSuccessful) {
                                    continuation.resumeWith(Result.success(Unit))
                                } else {
                                    taskUpdateReceived.exception?.let {
                                        continuation.resumeWith(Result.failure(it))
                                    }
                                }
                            }
                    } else {
                        taskUpdateSend.exception?.let {
                            continuation.resumeWith(Result.failure(it))
                        }

                    }
                }
        }
    }
}