package com.ladrope.venpix.controller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.ladrope.venpix.Adapters.MomentAdapter
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Moment
import kotlinx.android.synthetic.main.activity_my_moments.*

class MyMoments : AppCompatActivity() {

    var adapter: MomentAdapter? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var momentList: ArrayList<Moment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_moments)

        var title = intent.extras.get("albumTitle")

        albumTitle.text = title.toString()

        momentList = ArrayList<Moment>()

        adapter = MomentAdapter(momentList!!, this)
        layoutManager = GridLayoutManager(this, 4)

        moment_list.layoutManager = layoutManager
        moment_list.adapter = adapter

        //create dummy data
        for (i in 0..9){
            val moment = Moment("whatever", "i dont care", "http://www.informationng.com/wp-content/uploads/2012/04/563128_276441909116151_134391599987850_631577_2067204872_n.jpg", "hello")
            momentList?.add(moment)
        }

        adapter?.notifyDataSetChanged()
    }
}