package com.ladrope.venpix.services

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Moment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException




fun addToMyFavourites(moment: Moment, context: Context){
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid

    val databaseRef = FirebaseDatabase.getInstance()
                        .reference.child("users")
                        .child(currentUser)
                        .child("favourites")
                        .child(moment.key)
        databaseRef.setValue(moment).addOnCompleteListener {
            val momentDeleteFailed: Int = context.resources.getIdentifier("momentAddToFavSuc", "string", context.packageName)
            Toast.makeText(context,momentDeleteFailed, Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            val momentDeleteFailed: Int = context.resources.getIdentifier("momentAddToFavFail", "string", context.packageName)
            Toast.makeText(context,momentDeleteFailed, Toast.LENGTH_SHORT).show()
        }
}


// Returns the URI path to the Bitmap displayed in specified ImageView
fun getLocalBitmapUri(imageView: ImageView, context: Context): Uri? {
    // Extract Bitmap from ImageView drawable
    val drawable = imageView.getDrawable()

    var bmp: Bitmap? = null

    if (drawable is BitmapDrawable) {
        bmp = (imageView.getDrawable() as BitmapDrawable).bitmap
    } else {
        return null
    }
    // Store image to default external storage directory
    var bmpUri: Uri? = null
    try {
        val file = File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png")
        file.getParentFile().mkdirs()
        val out = FileOutputStream(file)
        bmp!!.compress(Bitmap.CompressFormat.PNG, 90, out)
        out.close()

        if(Build.VERSION.SDK_INT <= 24){
            bmpUri = Uri.fromFile(file)
        }else{
            bmpUri = FileProvider.getUriForFile(context, "com.ladrope.venpix.fileprovider", file)
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }

    return bmpUri
}


fun saveImage(any: Any?, context: Context){
    var iv: ImageView? = null
    if (any is ImageView){
        iv = any
        val draw = iv?.getDrawable() as BitmapDrawable
        val bitmap = draw.bitmap

        var outStream: FileOutputStream? = null
        val sdCard = Environment.getExternalStorageDirectory()
        val dir = File(sdCard.absolutePath + "/PhotoCollections")
        dir.mkdirs()
        val fileName = String.format("%d.jpg", System.currentTimeMillis())
        val outFile = File(dir, fileName)
        outStream = FileOutputStream(outFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.flush()
        outStream.close()

        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        var data: Uri? = null
        if(Build.VERSION.SDK_INT <= 24){
            data = Uri.fromFile(outFile)
        }else {
            data = FileProvider.getUriForFile(context, "com.ladrope.venpix.fileprovider", outFile)
        }
        intent.data = data
        context.sendBroadcast(intent)

        Toast.makeText(context, context.getString(R.string.imageSaved), Toast.LENGTH_SHORT).show()

    }

}

