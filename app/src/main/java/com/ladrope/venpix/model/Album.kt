package com.ladrope.venpix.model

/**
 * Created by USER on 1/12/18.
 */
class Album() {
    var albumTitle: String? = null
    var albumDesc: String? = null
    var creatorId: String? = null
    var creatorName: String? = null
    var image: String? = null
    var created_at: Long? = null
    var event_date: Long? = null

    constructor(albumTitle: String, albumDesc: String, creatorId: String, creatorName: String, created_at: Long, event_date: Long, image: String): this(){
        this.albumDesc = albumDesc
        this.albumTitle = albumTitle
        this.creatorId = creatorId
        this.creatorName = creatorName
        this.image = image
        this.created_at = created_at
        this.event_date = event_date
    }
}

