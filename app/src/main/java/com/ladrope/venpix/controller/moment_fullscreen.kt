package com.ladrope.venpix.controller

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
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
import com.ladrope.venpix.services.getLocalBitmapUri
import com.ladrope.venpix.utilities.PERMISSION_REQUEST_CODE
import com.ladrope.venpix.widget.ExtendedViewPager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_moment_fullscreen.*


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

        if (albumCreator != FirebaseAuth.getInstance().uid){
            fullScreenDeleteBtn.visibility = View.GONE
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
        if (momentList!![viewPager!!.currentItem].type =="VIDEO"){
            shareVideo()
        }else{
            shareImage()
        }
    }


    fun delete(view: View){
        val moment = momentList!![viewPager!!.currentItem]
        if (albumCreator == FirebaseAuth.getInstance().uid){
           deleteMoment(moment)

        }else{
            Toast.makeText(this,getString(R.string.onlyOwnerDel), Toast.LENGTH_SHORT).show()
        }
    }

    fun addToFavourites(view: View){
        val moment = momentList!![viewPager!!.currentItem]
        addToMyFavourites(moment, applicationContext)
    }

    private fun deleteMoment(moment: Moment) {


        var databaseRef: DatabaseReference? = null
        if (albumKey == "null"){
            databaseRef  = FirebaseDatabase.getInstance()
                    .reference.child("users")
                    .child(FirebaseAuth.getInstance().uid)
                    .child("favourites").child(moment.key)
        }else{
            databaseRef = FirebaseDatabase.getInstance()
                    .reference.child("albums").child(albumKey).child("moments").child(moment.key)
        }

        databaseRef?.setValue(null)?.addOnFailureListener {
            Toast.makeText(this,getString(R.string.momentDeleteFailure), Toast.LENGTH_SHORT).show()

        }?.addOnCompleteListener {
            Toast.makeText(this,getString(R.string.momentDeleteSuccess), Toast.LENGTH_SHORT).show()
            adapter?.deleteMoment(viewPager?.currentItem!!)
        }

    }

    fun shareVideo() {

//        val path = momentList!![viewPager!!.currentItem].url
//        MediaScannerConnection.scanFile(this, arrayOf(path),
//                null, object : MediaScannerConnection.OnScanCompletedListener {
//            override fun onScanCompleted(path: String, uri: Uri) {
//                Log.e(path,uri.toString())
//                val shareIntent = Intent(
//                        android.content.Intent.ACTION_SEND)
//                shareIntent.type = "video/*"
//                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//                shareIntent
//                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
//                startActivity(Intent.createChooser(shareIntent,
//                        getString(R.string.share_video)))
//
//            }
//        })
        Toast.makeText(this, getString(R.string.shareFail), Toast.LENGTH_SHORT).show()
    }

    fun shareImage(){
        val tempImageView = ImageView(this)
        Picasso.with(this).load(momentList!![viewPager!!.currentItem].url).into(tempImageView)

        val uri = getLocalBitmapUri(tempImageView, applicationContext)

        val share = Intent(Intent.ACTION_SEND)
        share.type = ("image/*, video/*")
        share.putExtra(Intent.EXTRA_STREAM, uri )
        startActivity(Intent.createChooser(share, getString(R.string.share_image)))

    }

    fun saveImage(view: View){
        if (!checkPermission()) {
            requestPermission()
        } else {
                val tempImageView = ImageView(this)
                Picasso.with(this).load(momentList!![viewPager!!.currentItem].url).into(tempImageView)
                com.ladrope.venpix.services.saveImage(tempImageView, this)
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {

                val readAccessGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                if (readAccessGranted) {
                    val tempImageView = ImageView(this)
                    Picasso.with(this).load(momentList!![viewPager!!.currentItem].url).into(tempImageView)
                    com.ladrope.venpix.services.saveImage(tempImageView, this)
                }

                else {

                    Toast.makeText(this, getString(R.string.permission), Toast.LENGTH_LONG).show()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showMessageOKCancel(getString(R.string.permission),
                                    DialogInterface.OnClickListener { dialog, which ->
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                                    PERMISSION_REQUEST_CODE)
                                        }
                                    })
                            return
                        }
                    }

                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {

        AlertDialog.Builder(this@moment_fullscreen)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

}

