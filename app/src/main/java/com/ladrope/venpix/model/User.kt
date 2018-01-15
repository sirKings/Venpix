package com.ladrope.venpix.model

/**
 * Created by USER on 1/13/18.
 */
class User() {
    var userName: String? = null
    var email: String? = null
    var userImage: String? = null

    constructor(userName: String?, email: String?, userImage: String?): this(){
        this.email = email
        this.userName = userName
        this.userImage = userImage
    }
}