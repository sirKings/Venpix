package com.ladrope.venpix.controller

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ladrope.venpix.R
import com.ladrope.venpix.utilities.LetterAvatar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*


class home : AppCompatActivity() {

    var editButton: ImageButton? = null
    var createAlbum: ImageView? = null
    var createMoments: ImageView? = null
    var myAlbums: ImageView? = null
    var myMoment: ImageView? = null
    val colorInt: Int = Color.parseColor("#252231")

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()

        editButton = editProfile
        editButton?.setColorFilter(colorInt)

        myMoment = myMoments

        createAlbum = createAlbums
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.currentUser
        updateUI(currentUser)
    }

    fun updateUI(user: FirebaseUser?){
        if(user?.photoUrl != null){
            Picasso.with(this).load(user?.photoUrl).into(profilePicture)
            username.text = user?.displayName
        }else {
            username.text = user?.displayName
            val initial = user?.displayName?.get(0)

            profilePicture.setImageDrawable(LetterAvatar(this, colorInt, initial, 15))
        }

    }
}
