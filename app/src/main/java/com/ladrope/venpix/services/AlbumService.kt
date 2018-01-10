package com.ladrope.venpix.services

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by USER on 12/18/17.
 */
@IgnoreExtraProperties
class Album(public val albumTitle: String?, public val albumDesc: String?, public val created_by: String?, public val album_creator: String?){
    open fun Album() {}
}

val albumRef = database.getReference("albums")

fun createAlbum(album: Album): String {

    val key = albumRef.push().key

    albumRef.child(key).setValue(album)

    return key
}

fun addMoment(albumKey: String, moment: Moment, key: String) {

    albumRef.child(albumKey).child(key).setValue(moment)
}

fun removeMoment( albumKey: String, momentKey: String){

    albumRef.child(albumKey).child(momentKey).removeValue()
}