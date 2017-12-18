package com.ladrope.venpix.services

import com.google.firebase.database.*

/**
 * Created by USER on 12/17/17.
 */
@IgnoreExtraProperties
class User(public val username: String?, public val email: String?){
    open fun User() {}
}

fun createUser(user: User, uid: String?){


    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("users")

    userRef.addListenerForSingleValueEvent(object:ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.child(uid).exists())
            {
                println("User already exists")
            }
            else
            {
                userRef.child(uid).setValue(user)
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            println("An error occurred")
        }
    })

}

fun updateUser() {

}

