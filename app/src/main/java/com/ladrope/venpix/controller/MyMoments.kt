package com.ladrope.venpix.controller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.ladrope.venpix.Adapters.MomentAdapter
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Moment
import com.ladrope.venpix.services.getLocalBitmapUri
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_my_moments.*
import kotlinx.android.synthetic.main.moment_row.*

class MyMoments : AppCompatActivity() {

    var adapter: MomentAdapter? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var options: FirebaseRecyclerOptions<Moment>? = null
    var albumKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_moments)

        val title = intent.extras.get("albumTitle")
        albumKey = intent.extras.get("albumKey") as String
        val albumCreator = intent.extras.get("albumCreator") as String

        albumTitle.text = title.toString()

        val database = FirebaseDatabase.getInstance()
        val query = database.getReference().child("albums").child(albumKey).child("moments")

        options = FirebaseRecyclerOptions.Builder<Moment>()
                .setQuery(query, Moment::class.java)
                .build()

        adapter = MomentAdapter(options!!, albumKey!!, albumCreator, this)
        layoutManager = GridLayoutManager(this, 4)

        moment_list.layoutManager = layoutManager
        moment_list.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    fun SelectState(view: View){
            momentListDeleteBtn.visibility = View.VISIBLE
            momentListShareBtn.visibility = View.VISIBLE
            cancel.visibility = View.VISIBLE
            momentListSelectBtn.visibility = View.GONE
            albumTitle.visibility = View.GONE
            adapter?.state = true
            adapter?.notifyDataSetChanged()
    }

    fun shareSelectedMoments(view: View){
        if (adapter!!.selectedMomentList.isNotEmpty()){
            share(adapter!!.selectedMomentList)
        }else{
            Toast.makeText(this, getString(R.string.nothingSelected), Toast.LENGTH_SHORT).show()
        }

    }

    fun share(list: ArrayList<Moment>){

        Log.e("SelectedMoments", list.toString())

        val uriList = ArrayList<Uri>()

        Log.e("UriSelected", uriList.toString())

        for (i in list){
            if (i.type == "VIDEO"){

            }else{
                val tempImageView = ImageView(this)
                Picasso.with(this).load(i.url).into(tempImageView)
                val uri = getLocalBitmapUri(tempImageView, applicationContext)
                Log.e("Uri", uri.toString())

                uriList.add(uriList.size, uri!!)
            }

        }

        val share = Intent(Intent.ACTION_SEND_MULTIPLE)
        share.type = "image/*"
        share.putExtra(Intent.EXTRA_STREAM, uriList )
        startActivity(Intent.createChooser(share, "Share Images"))

    }

    fun deleteSelectedMoment(view: View){
        for(moment in adapter!!.selectedMomentList){
            deleteMoment(moment)
        }
    }

    fun cancelSelection(view: View){
        momentListDeleteBtn.visibility = View.GONE
        momentListShareBtn.visibility = View.GONE
        momentListSelectBtn.visibility = View.VISIBLE
        cancel.visibility = View.GONE
        albumTitle.visibility = View.VISIBLE
        selectBox.visibility = View.GONE
        adapter?.state = false
        adapter?.notifyDataSetChanged()
    }

    fun deleteMoment(moment: Moment){
        val databaseRef = FirebaseDatabase.getInstance().reference.child("albums").child(albumKey).child("moments").child(moment.key)
        databaseRef.setValue(null)

    }

}
