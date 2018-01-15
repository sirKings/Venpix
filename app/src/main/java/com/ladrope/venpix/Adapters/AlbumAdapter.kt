package com.ladrope.venpix.Adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ladrope.venpix.R
import com.ladrope.venpix.controller.MyMoments
import com.ladrope.venpix.model.Album
import com.squareup.picasso.Picasso

/**
 * Created by USER on 1/9/18.
 */

class AlbumAdapter(private val list: ArrayList<Album>, private val context: Context): RecyclerView.Adapter<AlbumAdapter.ViewHolder>(){
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItem(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.album_row, parent, false)

        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItem(album: Album){
            val albumTitle = itemView.findViewById<TextView>(R.id.albumTitle)
            val albumDesc = itemView.findViewById<TextView>(R.id.albumDesc)
            val albumCreator = itemView.findViewById<TextView>(R.id.albumCreator)
            val albumLayout = itemView.findViewById<ImageView>(R.id.albumLayout)

            albumTitle.text = album.albumTitle
            albumCreator.text = album.creatorName

            if(album.albumDesc!!.length > 20){

                albumDesc.text = album.albumDesc!!.slice(0..20) + "..."
            }else{
                albumDesc.text = album.albumDesc
            }
            Picasso.with(context).load(R.drawable.garden).placeholder(R.drawable.progress_animation).into(albumLayout)

            itemView.setOnClickListener {
                var momentIntent = Intent(context, MyMoments::class.java)
                momentIntent.putExtra("albumTitle", albumTitle.text.toString())
                startActivity(context, momentIntent, null)
            }
        }
    }

}