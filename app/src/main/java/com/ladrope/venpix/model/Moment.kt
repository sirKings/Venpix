package com.ladrope.venpix.model

/**
 * Created by USER on 1/13/18.
 */
class Moment() {
    var created_at: String? = null
    var key: String? = null
    var url: String? = null
    var tag: String? = null
    var albumKey: String? = null
    var created_by: String? = null
    var isSelected = false
    var type: String? = null
    var length: Double? = null


    constructor(created_at: String, key: String, url: String, tag: String, albumKey: String, created_by: String, isSelected: Boolean, type: String, length: Double): this(){
        this.created_at = created_at
        this.key = key
        this.url = url
        this.tag = tag
        this.albumKey = albumKey
        this.created_by = created_by
        this.isSelected = isSelected
        this.type = type
        this.length = length
    }
}