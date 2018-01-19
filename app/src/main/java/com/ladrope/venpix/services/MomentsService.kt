package com.ladrope.venpix.services

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ladrope.venpix.model.Moment

fun addToMyFavourites(moment: Moment, context: Context){
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid

    val databaseRef = FirebaseDatabase.getInstance()
                        .reference.child("users")
                        .child(currentUser)
                        .child("favourites")
                        .child(moment.key)
        databaseRef.setValue(moment).addOnCompleteListener {
            val momentDeleteFailed: Int = context.resources.getIdentifier("momentAddToFavSuc", "string", context.packageName)
            Toast.makeText(context,momentDeleteFailed, Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            val momentDeleteFailed: Int = context.resources.getIdentifier("momentAddToFavFail", "string", context.packageName)
            Toast.makeText(context,momentDeleteFailed, Toast.LENGTH_SHORT).show()
        }
}

fun deleteMoment(moment: Moment, context: Context, albumKey: String) {

    var databaseRef: DatabaseReference? = null
    if (albumKey == "null"){
        databaseRef  = FirebaseDatabase.getInstance()
                .reference.child("users")
                .child(FirebaseAuth.getInstance().uid)
                .child("favourites").child(moment.key)
    }else{
        databaseRef = FirebaseDatabase.getInstance()
                .reference.child("albums").child(albumKey).child("moments").child(moment.key)
    }

    databaseRef?.setValue(null)?.addOnFailureListener {
        val momentDeleteFailed: Int = context.resources.getIdentifier("momentDeleteFailure", "string", context.packageName)
        Toast.makeText(context,momentDeleteFailed, Toast.LENGTH_SHORT).show()

    }?.addOnCompleteListener {
        val momentDeleteSucceed: Int = context.resources.getIdentifier("momentDeleteSuccess", "string", context.packageName)
        Toast.makeText(context,momentDeleteSucceed, Toast.LENGTH_SHORT).show()

    }

}