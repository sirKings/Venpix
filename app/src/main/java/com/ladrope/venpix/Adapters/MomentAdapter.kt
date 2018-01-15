package com.ladrope.venpix.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Moment
import com.squareup.picasso.Picasso

/**
 * Created by USER on 1/10/18.
 */
class MomentAdapter(private val list: ArrayList<Moment>, private val context: Context): RecyclerView.Adapter<MomentAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.moment_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItem(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItem(moment: Moment){
            val momentImage = itemView.findViewById<ImageView>(R.id.momentLayout)

            Picasso.with(context).load(moment.url).placeholder(R.drawable.progress_animation).into(momentImage);

            itemView.setOnClickListener {

            }
        }
    }
}