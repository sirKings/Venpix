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
    private var myImageObserver: MyImageObserver? = null
    private var myVideoObserver: MyVideoObserver? = null
    private var albumKey: String? = null
    private var uid: String? = null
    private  var prevImageAdded: String? = null
    private var prevVideoAdded: String? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Let it continue running until it is stopped.
        albumKey = intent?.extras?.getString("albumKey")
        uid = intent?.extras?.getString("uid")

        myImageObserver = MyImageObserver(Handler(), applicationContext)
        myVideoObserver = MyVideoObserver(Handler(), applicationContext)

        contentResolver.registerContentObserver(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI,true, myImageObserver)
        contentResolver.registerContentObserver(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, myImageObserver)

        contentResolver.registerContentObserver(android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI,true, myVideoObserver)
        contentResolver.registerContentObserver(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, myVideoObserver)

        Log.e("Service", "Started")
        Log.e("albumKey", albumKey)
        return Service.START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Service", "Destroyed")
        contentResolver.unregisterContentObserver(myImageObserver)


    }

    inner class MyImageObserver(handler: Handler,  private val context: Context) : ContentObserver(handler){

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            Log.d("ImageObserver", "Internal Media has been changed" + albumKey)
            super.onChange(selfChange)
            val imageUri = readLastDateFromMediaStore(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // comapare with your stored last value and do what you need to do
            Log.e("timestamp", imageUri)
            if (imageUri == "Nothing found"){

            }else{
                uploadImage(imageUri, "IMAGE")
            }



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

    fun uploadImage(uri: String, type: String){
        val imageName = getName(uri)

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
                val url = resultData?.get("url") as String

                if ( url == prevImageAdded || url == prevVideoAdded ){

                }else{
                    val dataRef = FirebaseDatabase.getInstance().reference.child("albums").child(albumKey).child("moments")
                    val key = dataRef.push().key

                    val moment = Moment()
                    moment.created_by = uid
                    moment.albumKey = albumKey
                    moment.url = url
                    moment.created_at = System.currentTimeMillis().toString()
                    moment.key = key
                    moment.type = type

                    dataRef.child(key).setValue(moment).addOnCompleteListener { Log.e("Moment Service", "Added successfull") }
                            .addOnFailureListener { Log.e("Service Moment", "Not added") }
                    if (type == "IMAGE"){
                        prevImageAdded = url
                    }else{
                        prevVideoAdded = url
                    }

                }
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
            }
        }




        var requestid = MediaManager.get().upload(uri).unsigned("kfmwfbua").option("public_id", uid.toString()+imageName).option("resource_type", "auto").callback(callback()).dispatch()

    }

    fun getName(uri: String): String{
        val index = uri.lastIndexOf('/')

        val tempName = uri.substring(index + 1)
        val formatIndex = tempName.lastIndexOf('.')

        val name = tempName.substring(0, formatIndex)

        return name
    }

    //video handlers

    inner class MyVideoObserver(handler: Handler,  private val context: Context) : ContentObserver(handler){

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            Log.e("VideoObserver", "Internal Media has been changed" + albumKey)
            super.onChange(selfChange)
            val videoUri = readLastDateFromMediaStore(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            // comapare with your stored last value and do what you need to do
            Log.e("timestamp", videoUri)
            if (videoUri == "Nothing found"){

            }else{
                uploadImage(videoUri, "VIDEO")
            }


        }

        override fun deliverSelfNotifications(): Boolean {
            return true
        }

        private fun readLastDateFromMediaStore(context: Context, uri: Uri): String {
            var cursor: Cursor? = null

            try {
                val proj = arrayOf(MediaStore.Video.Media.DATA)
                cursor = context.contentResolver.query(uri, proj, null, null, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC")
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
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
}