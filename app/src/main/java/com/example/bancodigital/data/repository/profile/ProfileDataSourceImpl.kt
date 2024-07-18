package com.example.bancodigital.data.repository.profile

import com.example.bancodigital.data.model.User
import com.example.bancodigital.util.FirebaseHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class ProfileDataSourceImpl @Inject constructor(
    databse: FirebaseDatabase
) : ProfileDataSource {

    private val profileReference = databse.reference
        .child("profile")

    override suspend fun saveProfile(user: User) {
        return suspendCoroutine { continuation ->
            profileReference
                .child(FirebaseHelper.getUserId())
                .setValue(user).addOnCompleteListener { task ->
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

    override suspend fun getProfile(): User {
        return suspendCoroutine { continuation ->
            profileReference
                .child(FirebaseHelper.getUserId())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let { continuation.resumeWith(Result.success(it)) }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWith(Result.failure(error.toException()))
                    }
                })
        }
    }

    override suspend fun getProfileList(): List<User> {
        return suspendCoroutine { continuation ->
            profileReference
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userList: MutableList<User> = mutableListOf()
                        for (ds in snapshot.children) {
                            val user = ds.getValue(User::class.java)
                            user?.let { userList.add(it) }
                        }
                        continuation.resumeWith(Result.success(
                            userList.apply {
                                removeIf { it.id == FirebaseHelper.getUserId() }
                            }
                        ))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWith(Result.failure(error.toException()))
                    }
                })
        }
    }
}