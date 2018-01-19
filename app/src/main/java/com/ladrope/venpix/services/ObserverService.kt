package com.ladrope.venpix.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log


/**
 * Created by USER on 1/18/18.
 */
class ObserverService: Service() {
    private var myObserver: MyObserver? = null
    private var albumKey: String? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Let it continue running until it is stopped.
        albumKey = intent?.extras?.get("albumKey") as String

        myObserver = MyObserver(Handler(), applicationContext)

        contentResolver.registerContentObserver(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI,true, myObserver)
        contentResolver.registerContentObserver(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, myObserver)

        Log.e("Service", "Started")
        Log.e("albumKey", albumKey)
        return Service.START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Service", "Destroyed")
        contentResolver.unregisterContentObserver(myObserver)

    }

    inner class MyObserver(handler: Handler,  private val context: Context) : ContentObserver(handler){

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            Log.d("your_tag", "Internal Media has been changed" + albumKey)
            super.onChange(selfChange)
            val timestamp = readLastDateFromMediaStore(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // comapare with your stored last value and do what you need to do
            Log.e("timestamp", timestamp)
        }

        private fun readLastDateFromMediaStore(context: Context, uri: Uri): String {
            var cursor: Cursor? = null

            try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(uri, proj, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                var lastAdded = "Nothing found"
                if(cursor.moveToFirst()){
                    lastAdded = cursor!!.getString(column_index)
                }
                return lastAdded
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }

        }
    }
}