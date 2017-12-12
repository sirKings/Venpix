package com.ladrope.venpix.utilities

/**
 * Created by USER on 12/12/17.
 */

fun isValidEmail(target: String?): Boolean {
    return if (target == null) {
        false
    } else {
        android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}