package com.ladrope.venpix.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.util.Log
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

    var state = false
    val selectedMomentList = ArrayList<Moment>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Moment) {
        holder.bindItem(model)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.moment_row, parent, false)
        return ViewHolder(view)
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItem(moment: Moment){
            val momentImage = itemView.momentLayout
            val thumb: String

            if (moment.type == "VIDEO"){
                thumb = getVideoThumbUrl(moment.url!!)
            }else{
                thumb = getImageThumbUrl(moment.url!!)
            }
            Log.e("thumbnail", thumb)
            Picasso.with(context).load(thumb).placeholder(R.drawable.profile_img).into(momentImage)
            if (state){

                itemView.selectBox.visibility = View.VISIBLE
                if (moment.isSelected){
                    itemView.selectBox.isChecked = true
                }

                itemView.setOnClickListener {
                    if (moment.isSelected){
                        itemView.selectBox.isChecked = false
                        moment.isSelected = false
                        selectedMomentList.remove(moment)
                        itemView.momentLayout.clearColorFilter()
                    }else{
                        itemView.selectBox.isChecked = true
                        moment.isSelected = true
                        selectedMomentList.add(moment)
                        itemView.momentLayout.setColorFilter(R.color.colorAccent, PorterDuff.Mode.OVERLAY)
                    }
                }
            }else{
                itemView.setOnClickListener {
                    val fullScreenIntent = Intent(context, moment_fullscreen::class.java)
                    fullScreenIntent.putExtra("position", adapterPosition)
                    fullScreenIntent.putExtra("albumKey", albumkey)
                    fullScreenIntent.putExtra("albumCreator", albumCreator)
                    context.startActivity(fullScreenIntent)

                }
            }

        }
    }


    fun getImageThumbUrl(url: String): String{
        val secondPart = url.substring(47)
        val firstPart = url.substring(0, 47)
        val control = "w_100,c_scale/"
        return firstPart + control + secondPart
    }

    fun getVideoThumbUrl(url: String): String{
        val index = url.lastIndexOf('.')
        val firstPart = url.substring(0, index+1)
        val control = "jpg"
        return firstPart + control
    }
}