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
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.database.FirebaseDatabase
import com.ladrope.venpix.model.Moment


/**
 * Created by USER on 1/18/18.
 */
class ObserverService: Service() {
    private var myObserver: MyObserver? = null
    private var albumKey: String? = null
    private var uid: String? = null
    private  var prevAdded: String? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Let it continue running until it is stopped.
        albumKey = intent?.extras?.getString("albumKey")
        uid = intent?.extras?.getString("uid")

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

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            Log.d("your_tag", "Internal Media has been changed" + albumKey)
            super.onChange(selfChange)
            val timestamp = readLastDateFromMediaStore(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // comapare with your stored last value and do what you need to do
            Log.e("timestamp", timestamp)
            uploadImage(timestamp)


        }

        override fun deliverSelfNotifications(): Boolean {
            return true
        }

        private fun readLastDateFromMediaStore(context: Context, uri: Uri): String {
            var cursor: Cursor? = null

            try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(uri, proj, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val id = cursor!!.getColumnIndex("_id")
                Log.e("ID", id.toString())
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

    fun uploadImage(uri: String){
        val imageName = getImageName(uri)

        class callback: UploadCallback {
            override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                Log.e("Service Upload", "Rescheduled")
            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                Log.e("Service Upload", "Error")
            }

            override fun onStart(requestId: String?) {
                Log.e("Service Upload", "Started")
            }

            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                Log.e("Service Upload", "Successful")
                var url = resultData?.get("url") as String

                if ( url == prevAdded ){

                }else{
                    val dataRef = FirebaseDatabase.getInstance().reference.child("albums").child(albumKey).child("moments")
                    val key = dataRef.push().key

                    val moment = Moment()
                    moment.created_by = uid
                    moment.albumKey = albumKey
                    moment.url = url
                    moment.created_at = System.currentTimeMillis().toString()
                    moment.key = key

                    dataRef.child(key).setValue(moment).addOnCompleteListener { Log.e("Moment Service", "Added successfull") }
                            .addOnFailureListener { Log.e("Service Moment", "Not added") }
                    prevAdded = url
                }
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
            }
        }


        var requestid = MediaManager.get().upload(uri).unsigned("kfmwfbua").option("public_id", uid.toString()+imageName).callback(callback()).dispatch()

    }

    fun getImageName(uri: String): String{
        val index = uri.lastIndexOf('/')

        val name = uri.substring(index + 1)
        return name
    }
}