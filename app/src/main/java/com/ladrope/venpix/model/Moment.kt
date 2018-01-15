package com.ladrope.venpix.model

/**
 * Created by USER on 1/13/18.
 */
class Moment() {
    var created_at: String? = null
    var key: String? = null
    var url: String? = null
    var tag: String? = null

    constructor(created_at: String, key: String, url: String, tag: String): this(){
        this.created_at = created_at
        this.key = key
        this.url = url
        this.tag = tag
    }
}