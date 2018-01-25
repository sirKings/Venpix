package com.ladrope.venpix.controller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ladrope.venpix.Adapters.AlbumAdapter
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Album
import kotlinx.android.synthetic.main.activity_my_albums.*

class MyAlbums : AppCompatActivity() {

    var adapter: AlbumAdapter? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var options: FirebaseRecyclerOptions<Album>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_albums)

        val uid = FirebaseAuth.getInstance().uid


        val database = FirebaseDatabase.getInstance()
        val query = database.getReference("users").child(uid).child("albums")


        options = FirebaseRecyclerOptions.Builder<Album>()
                .setQuery(query, Album::class.java)
                .build()


        adapter = AlbumAdapter(options!!, this)
        layoutManager = LinearLayoutManager(this)

        //set up recycler view
        album_list.layoutManager = layoutManager
        album_list.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

}
