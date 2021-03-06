package com.ladrope.venpix.controller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageButton
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Album
import com.ladrope.venpix.utilities.ADD_ALBUM_LINK
import com.ladrope.venpix.utilities.LetterAvatar
import com.ladrope.venpix.utilities.NewAlbum
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*


class home : AppCompatActivity() {

    var editButton: ImageButton? = null
    var mDatabase: DatabaseReference? = null
    var queue: RequestQueue? = null


    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()

        mDatabase = FirebaseDatabase.getInstance().reference.child("users").child(mAuth?.uid)

        editButton = editProfile
        editButton?.setColorFilter(R.color.colorPrimary)

        val currentUser = mAuth?.currentUser
        updateUI(currentUser)

        queue = Volley.newRequestQueue(this)

    }


    fun updateUI(user: FirebaseUser?){
        mDatabase!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val displayName = dataSnapshot!!.child("userName").value.toString()
                val image = dataSnapshot!!.child("userImage").value.toString()
                val coverImage = dataSnapshot!!.child("coverImage").value.toString()

                username.text = displayName

                if (image!!.equals("default")) {
                    val initial = user?.displayName?.get(0)
                    profilePicture.setImageDrawable(LetterAvatar(this@home, Color.parseColor("#252231"), initial, 15))
                }else{
                    Picasso.with(this@home)
                            .load(image)
                            .placeholder(R.drawable.profile_img)
                            .into(profilePicture)
                }

                if (coverImage!!.equals("default")) {
                    Picasso.with(this@home)
                            .load(R.drawable.garden)
                            .into(coverPicture)
                }else{
                    Picasso.with(this@home)
                            .load(coverImage)
                            .placeholder(R.drawable.garden)
                            .into(coverPicture)
                }


            }

            override fun onCancelled(databaseErrorSnapshot: DatabaseError?) {

            }

        })

    }

    fun settings(view: View){
        val settingIntent = Intent(this@home, Settings::class.java)
        startActivity(settingIntent)
    }

    fun createAlbum(view: View){

        val createAlbumIntent = Intent(this@home, create_album::class.java)
        startActivity(createAlbumIntent)
    }

    fun captureMoments(view: View){

        val captureIntent = Intent(this@home, CaptureMoment::class.java)
        startActivity(captureIntent)
    }

    fun myAlbums(view: View){

        val myAlbumIntent = Intent(this@home, MyAlbums::class.java)
        startActivity(myAlbumIntent)
    }

    fun myMoments( view: View){
        val myMomentsIntent = Intent(this@home, UserMoments::class.java)
        myMomentsIntent.putExtra("albumTitle", "My Momments")
        startActivity(myMomentsIntent)
    }

    fun sendAlbum(uid: String, album: Album){

        val postRequest = object : StringRequest(Request.Method.POST, ADD_ALBUM_LINK,
                object : Response.Listener<String> {
                    override fun onResponse(response: String) {
                        // response
                        Log.d("Response", response)
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        // error
                        Log.d("Error.Response", error.toString())
                    }
                }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["uid"] = uid
                params["albumKey"] = album.albumKey!!
                params["creatorName"] = album.creatorName!!
                params["albumTitle"] = album.albumTitle!!
                params["albumDesc"] = album.albumDesc!!
                params["plan"] = album.plan!!.toString()
                params["event_date"] = album.event_date!!.toString()
                params["creatorId"] = album.creatorId!!

                return params
            }
        }
        queue?.add(postRequest)
    }

    override fun onPause() {
        super.onPause()
        if(NewAlbum != null){
            sendAlbum(mAuth?.uid!!, NewAlbum!!)
            NewAlbum = null
        }
    }
}
