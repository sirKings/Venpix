package com.ladrope.venpix.controller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ladrope.venpix.R
import com.ladrope.venpix.utilities.LetterAvatar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*


class home : AppCompatActivity() {

    var editButton: ImageButton? = null
    val colorInt: Int = Color.parseColor("#252231")
    var mDatabase: DatabaseReference? = null

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()

        mDatabase = FirebaseDatabase.getInstance().reference.child("users").child(mAuth?.uid)

        editButton = editProfile
        editButton?.setColorFilter(colorInt)


    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.currentUser
        updateUI(currentUser)
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
                    profilePicture.setImageDrawable(LetterAvatar(this@home, colorInt, initial, 15))
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

}
