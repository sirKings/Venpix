package com.ladrope.venpix.services

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by USER on 12/18/17.
 */
@IgnoreExtraProperties
class Album(public val albumTitle: String?, public val albumDesc: String?, public val created_by: String?){
    open fun Album() {}
}

fun createAlbum(album: Album): String{


    val database = FirebaseDatabase.getInstance()
    val albumRef = database.getReference("albums")

    var key = albumRef.push().key

    albumRef.child(key).setValue(album)

    return key
}