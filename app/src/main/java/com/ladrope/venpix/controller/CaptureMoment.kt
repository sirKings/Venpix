package com.ladrope.venpix.controller

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ladrope.venpix.Adapters.AlbumSpinnerAdapter
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Album
import com.ladrope.venpix.services.ObserverService
import com.ladrope.venpix.utilities.MY_SHAREDPREF_NAME
import com.ladrope.venpix.utilities.PERMISSION_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_capture_moment.*


class CaptureMoment : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var adapter: AlbumSpinnerAdapter? = null
    var albumList: ArrayList<Album>? = null
    private var selectedAlbum: String? = null
    private var buttonStatus: Boolean? = null
    private var itemSetup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_moment)

        albumList = ArrayList()

        getAlbums()

        adapter = AlbumSpinnerAdapter(albumList!!, applicationContext)


        albumListSpinner.adapter = adapter
        albumListSpinner.onItemSelectedListener = this

    }

    fun switchClicked(view: View){

        if (selectedAlbum == null){
            Toast.makeText(this,R.string.select_an_album,Toast.LENGTH_SHORT).show()
            captureMomentSwitch.isChecked = false

        }else{
            if (captureMomentSwitch.isChecked){
                Log.e("Switch", "Checked")
                if(!checkPermission()){
                    requestPermission()
                }

                buttonStatus = captureMomentSwitch.isChecked
                startService()
            }
            else {

                Log.e("Switch", "UnChecked")
                buttonStatus = captureMomentSwitch.isChecked
                stopService()
            }
        }
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        selectedAlbum = albumList!![position].albumKey

        if(itemSetup){
            captureMomentSwitch.isChecked = false
        }else{
            itemSetup = true
        }

    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }

    private fun getAlbums(){
        var albumRef: DatabaseReference? = null

            albumRef = FirebaseDatabase
                    .getInstance()
                    .reference
                    .child("users")
                    .child(FirebaseAuth.getInstance().uid)
                    .child("albums")

        albumRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                for (data in p0!!.getChildren()) {
                    val album = data.getValue(Album::class.java)

                    albumList!!.add(album!!)
                    adapter?.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val prefs = getSharedPreferences(MY_SHAREDPREF_NAME, Context.MODE_PRIVATE)
        val restoredText = prefs.getBoolean("ButtonStatus", false)
        buttonStatus = restoredText
        captureMomentSwitch.isChecked = buttonStatus!!
        Log.e("buttonStatus", buttonStatus.toString())
    }

    override fun onPause() {
        super.onPause()
        val editor = getSharedPreferences(MY_SHAREDPREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean("ButtonStatus", buttonStatus!!)
        Log.e("buttonStatus", buttonStatus.toString())
        editor.apply()
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {

                val readAccessGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                if (readAccessGranted)

                else {

                    Toast.makeText(this, getString(R.string.permission), Toast.LENGTH_LONG).show()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                            showMessageOKCancel(getString(R.string.permission),
                                    DialogInterface.OnClickListener { dialog, which ->
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE),
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

        AlertDialog.Builder(this@CaptureMoment)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    fun startService() {
        val serviceIntent = Intent(baseContext, ObserverService::class.java)
        serviceIntent.putExtra("albumKey", selectedAlbum)
        serviceIntent.putExtra("uid", FirebaseAuth.getInstance().uid)
        startService(serviceIntent)

    }

    // Method to stop the service
    fun stopService() {
        stopService(Intent(baseContext, ObserverService::class.java))
    }
}
