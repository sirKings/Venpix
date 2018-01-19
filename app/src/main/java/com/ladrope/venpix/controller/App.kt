package com.ladrope.venpix.controller

import android.app.Application
import com.cloudinary.android.MediaManager
import io.branch.referral.Branch

/**
 * Created by USER on 12/18/17.
 */

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize the Branch object
        Branch.getAutoInstance(this)

        // Cloudinary Initialization
        MediaManager.init(this)
    }



}