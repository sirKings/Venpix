package com.ladrope.venpix.controller

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ladrope.venpix.Adapters.FullScreenAdapter
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Moment
import com.ladrope.venpix.services.addToMyFavourites
import com.ladrope.venpix.services.deleteMoment
import com.ladrope.venpix.widget.ExtendedViewPager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_moment_fullscreen.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class moment_fullscreen : AppCompatActivity() {

    var adapter: FullScreenAdapter? = null
    var viewPager: ExtendedViewPager? = null
    var momentList: ArrayList<Moment>? = null
    var position: Int? = null
    var albumCreator: String? = null
    var albumKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        setContentView(R.layout.activity_moment_fullscreen)

        position = intent.extras.get("position") as Int
        albumKey = intent.extras.get("albumKey") as String
        albumCreator = intent.extras.get("albumCreator") as String


        momentList = ArrayList()

        getMoments(albumKey!!)

        viewPager = fullscreenviewPager
        adapter = FullScreenAdapter(momentList!!)

        viewPager?.adapter = adapter

        if (albumKey == "null"){
            fullScreenFavouriteBtn.visibility = View.GONE
        }

    }

    private fun getMoments(albumKey: String){
        var momentRef: DatabaseReference? = null


        if (albumKey == "null"){
            momentRef = FirebaseDatabase.getInstance()
                    .reference.child("users")
                    .child(FirebaseAuth.getInstance().uid)
                    .child("favourites")
        }else{
            momentRef = FirebaseDatabase
                    .getInstance()
                    .reference
                    .child("albums")
                    .child(albumKey)
                    .child("moments")
        }
        momentRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                for (data in p0!!.getChildren()) {
                    val moment = data.getValue(Moment::class.java)

                    momentList!!.add(moment!!)
                    adapter?.notifyDataSetChanged()
                    viewPager?.currentItem = position!!
                }
            }
        })
    }

    fun share(view: View){
        var tempImageView = ImageView(this)
        Picasso.with(this).load(momentList!![viewPager!!.currentItem].url).into(tempImageView)

        val uri = getLocalBitmapUri(tempImageView)

        val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"
            share.putExtra(Intent.EXTRA_STREAM, uri )
            startActivity(Intent.createChooser(share, "Share Image"))

    }


    // Returns the URI path to the Bitmap displayed in specified ImageView
    fun getLocalBitmapUri(imageView: ImageView): Uri? {
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
            val file = File(applicationContext.getExternalFilesDir(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png")
            file.getParentFile().mkdirs()
            val out = FileOutputStream(file)
            bmp!!.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()

            if(Build.VERSION.SDK_INT <= 24){
                bmpUri = Uri.fromFile(file)
            }else{
                bmpUri = FileProvider.getUriForFile(this, "com.ladrope.venpix.fileprovider", file)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bmpUri
    }

    fun delete(view: View){
        val moment = momentList!![viewPager!!.currentItem]
        if (albumCreator == FirebaseAuth.getInstance().uid){
            deleteMoment(moment, applicationContext, albumKey!!)
        }else{
            Toast.makeText(this,getString(R.string.onlyOwnerDel), Toast.LENGTH_SHORT).show()
        }
    }

    fun addToFavourites(view: View){
        val moment = momentList!![viewPager!!.currentItem]
        addToMyFavourites(moment, applicationContext)
    }
}

