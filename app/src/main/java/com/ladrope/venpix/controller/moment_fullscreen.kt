package com.ladrope.venpix.controller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ladrope.venpix.Adapters.FullScreenAdapter
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Moment
import com.ladrope.venpix.widget.ExtendedViewPager
import kotlinx.android.synthetic.main.activity_moment_fullscreen.*

class moment_fullscreen : AppCompatActivity() {

    var adapter: FullScreenAdapter? = null
    var viewPager: ExtendedViewPager? = null
    var momentList: ArrayList<Moment>? = null
    var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        setContentView(R.layout.activity_moment_fullscreen)

        position = intent.extras.get("position") as Int
        val albumKey = intent.extras.get("albumKey") as String
        Log.e("Position", position.toString())

        momentList = ArrayList()

        getMoments(albumKey)

        viewPager = fullscreenviewPager
        adapter = FullScreenAdapter(momentList!!)

        viewPager?.adapter = adapter

    }

    fun getMoments(albumKey: String){
        var momentRef = FirebaseDatabase
                            .getInstance()
                            .reference
                            .child("albums")
                            .child(albumKey)
                            .child("moments")
        momentRef.addValueEventListener(object : ValueEventListener {
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
}

