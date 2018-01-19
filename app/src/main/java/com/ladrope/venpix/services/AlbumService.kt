package com.ladrope.venpix.services

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Album

/**
 * Created by USER on 12/18/17.
 */


fun addAlbum(uid: String, album: Album) {
    val database = FirebaseDatabase.getInstance()

    val userAlbumRef = database.getReference("users").child(uid).child("albums").child(album.albumKey)

    userAlbumRef.setValue(album)
}

fun removeAlbum(uid: String?, albumKey: String?, context: Context){
    val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("albums").child(albumKey)
    databaseRef.setValue(null).addOnFailureListener {
        Toast.makeText(context, context.getString(R.string.albumDeleteFail), Toast.LENGTH_SHORT).show()
    }.addOnCompleteListener {
        Toast.makeText(context, context.getString(R.string.albumDeleteSucc), Toast.LENGTH_SHORT).show()
    }
}

fun shareAlbum(album: Album, context: Context){
    val share = Intent(Intent.ACTION_SEND)
    share.type = "text/*"
    share.putExtra(Intent.EXTRA_TEXT, album.albumLink)
    context.startActivity(Intent.createChooser(share, "Share Album Link"))
}

fun addAlbumLink( albumKey: String?, uid: String?, link: String){
    val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("albums").child(albumKey)
    val updateObj = HashMap<String?, Any>()
    updateObj.put("albumLink", link)
    databaseRef.updateChildren(updateObj)
}