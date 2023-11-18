package com.example.bancodigital.data.repository.profile

import android.net.Uri
import com.example.bancodigital.data.model.User
import com.example.bancodigital.util.FirebaseHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class ProfileDataSourceImpl @Inject constructor(
    database: FirebaseDatabase,
    storage: FirebaseStorage
    ) : ProfileDataSource{

    private val dataBaseReference = database.reference
        .child("profile")

    private val storageReference = storage.reference
        .child("images")
        .child("profiles")
        .child("${FirebaseHelper.getUserId()}.jpeg")


    override suspend fun saveProfile(user: User) {
        return suspendCoroutine {continuation ->
            dataBaseReference
                .child(FirebaseHelper.getUserId())
                .setValue(user).addOnCompleteListener {task->
                if (task.isSuccessful) {
                    continuation.resumeWith(Result.success(Unit))
                } else {
                    task.exception?.let {
                        continuation.resumeWith(Result.failure(it))
                    }
                }
            }
        }
    }

    override suspend fun getProfile(id: String): User {
        return suspendCoroutine { continuation ->
            dataBaseReference
                .child(id)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        continuation.resumeWith(Result.success(it))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWith(Result.failure(error.toException()))
                }

            })
        }
    }

    override suspend fun getProfileList(): List<User> {
        return suspendCoroutine { continuation ->
            dataBaseReference
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userList: MutableList<User> = mutableListOf()

                        for(ds in snapshot.children){
                            val user = ds.getValue(User::class.java)
                            user?.let {
                                userList.add(it)
                            }
                        }

                        continuation.resumeWith(Result.success(
                            userList.apply {
                                removeAll{
                                    it.id == FirebaseHelper.getUserId()
                                }
                            }
                        ))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWith(Result.failure(error.toException()))
                    }

                })
        }
    }

    override suspend fun saveImage(imageProfile: String): String {
        return suspendCoroutine {continuation ->
            val uploadTask = storageReference.putFile(Uri.parse(imageProfile))
            uploadTask.addOnSuccessListener {
                storageReference.downloadUrl.addOnCompleteListener {
                    continuation.resumeWith(Result.success(it.result.toString()))
                }
            }.addOnFailureListener{
                continuation.resumeWith(Result.failure(it))
            }
        }
    }

}