package com.ladrope.venpix.services

import com.google.firebase.database.FirebaseDatabase
import com.ladrope.venpix.model.Album

/**
 * Created by USER on 12/18/17.
 */


fun addAlbum(uid: String, album: Album) {
    val database = FirebaseDatabase.getInstance()
    val userAlbumRef = database.getReference("users").child(uid).child("albums").push()

    userAlbumRef.setValue(album)
}

fun removeAlbum(albumKey: String, uid: String){

}
