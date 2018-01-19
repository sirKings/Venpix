package com.ladrope.venpix.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.ladrope.venpix.R
import com.ladrope.venpix.controller.moment_fullscreen
import com.ladrope.venpix.model.Moment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.moment_row.view.*

/**
 * Created by USER on 1/10/18.
 */
class MomentAdapter(options: FirebaseRecyclerOptions<Moment>, private val albumkey: String, private val albumCreator: String, private val context: Context): FirebaseRecyclerAdapter<Moment, MomentAdapter.ViewHolder>(options){
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Moment) {
        holder?.bindItem(model)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.moment_row, parent, false)
        return ViewHolder(view)
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItem(moment: Moment){
            val momentImage = itemView.momentLayout

            Picasso.with(context).load(moment.url).placeholder(R.drawable.profile_img).into(momentImage);

            itemView.setOnClickListener {
                val fullScreenIntent = Intent(context, moment_fullscreen::class.java)
                fullScreenIntent.putExtra("position", adapterPosition)
                fullScreenIntent.putExtra("albumKey", albumkey)
                fullScreenIntent.putExtra("albumCreator", albumCreator)
                context.startActivity(fullScreenIntent)

            }

            itemView.setOnLongClickListener(object:View.OnLongClickListener {
                override fun onLongClick(v:View):Boolean {
                    itemView.momentLayout.setColorFilter(R.color.colorAccent, PorterDuff.Mode.OVERLAY)
                    return true
                }
            })

        }
    }
}