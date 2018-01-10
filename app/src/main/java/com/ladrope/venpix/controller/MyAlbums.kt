package com.ladrope.venpix.controller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.ladrope.venpix.Adapters.AlbumAdapter
import com.ladrope.venpix.R
import com.ladrope.venpix.services.Album
import kotlinx.android.synthetic.main.activity_my_albums.*

class MyAlbums : AppCompatActivity() {

    var adapter: AlbumAdapter? = null
    var albumList: ArrayList<Album>? = null
    var layoutManager: RecyclerView.LayoutManager? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_albums)

        albumList = ArrayList()
        adapter = AlbumAdapter(albumList!!, this)
        layoutManager = LinearLayoutManager(this)

        //set up recycler view
        album_list.layoutManager = layoutManager
        album_list.adapter = adapter

        //create dummy data
        for (i in 0..9){
            val album = Album("name"+i, "long stodhdh fhfhf hskwyedbdhfh hdbdhd hdbd", "helloe", "Kingso"+i)
            albumList?.add(album)
        }

        adapter?.notifyDataSetChanged()
    }
}
