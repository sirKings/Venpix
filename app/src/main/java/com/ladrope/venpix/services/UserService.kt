package com.ladrope.venpix.services

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ladrope.venpix.model.Moment

/**
 * Created by USER on 12/17/17.
 */


val database = FirebaseDatabase.getInstance()
val userRef = database.getReference("users")

fun createUser(user: com.ladrope.venpix.model.User, uid: String?){

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

fun addMoment(uid: String?, moment: Moment, key: String) {
    userRef.child(uid).child("moments").child(key).setValue(moment)
}

fun addAlbum(albumKey: String, uid: String?) {
    val key = userRef.child(uid).child("albums").push().key

    userRef.child(uid).child("albums").child(key).setValue(albumKey)
}

fun removeMoment() {

}

fun removeAlbum() {

}