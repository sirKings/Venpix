package com.ladrope.venpix.services

import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by USER on 12/18/17.
 */
@IgnoreExtraProperties
class Moment(public val name: String?, public val created_at: String?, public val url: Uri?, public var key: String){
    open fun Moment() {}
}
