package com.ladrope.venpix.controller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.ladrope.venpix.Adapters.MomentAdapter
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Moment
import kotlinx.android.synthetic.main.activity_my_moments.*

class MyMoments : AppCompatActivity() {

    var adapter: MomentAdapter? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var options: FirebaseRecyclerOptions<Moment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_moments)

        val title = intent.extras.get("albumTitle")
        val albumKey = intent.extras.get("albumKey") as String
        val albumCreator = intent.extras.get("albumCreator") as String

        albumTitle.text = title.toString()

        val database = FirebaseDatabase.getInstance()
        val query = database.getReference().child("albums").child(albumKey).child("moments")

        options = FirebaseRecyclerOptions.Builder<Moment>()
                .setQuery(query, Moment::class.java)
                .build()

        adapter = MomentAdapter(options!!, albumKey, albumCreator, this)
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
        if (adapter!!.state){
            momentListDeleteBtn.visibility = View.GONE
            momentListShareBtn.visibility = View.GONE
            momentListSelectBtn.visibility = View.VISIBLE
            adapter?.state = false
            adapter?.notifyDataSetChanged()
        }else {
            momentListDeleteBtn.visibility = View.VISIBLE
            momentListShareBtn.visibility = View.VISIBLE
            momentListSelectBtn.visibility = View.GONE
            adapter?.state = true
            adapter?.notifyDataSetChanged()
        }

    }

}
