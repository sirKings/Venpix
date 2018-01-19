package com.ladrope.venpix.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ladrope.venpix.R
import com.ladrope.venpix.controller.MyMoments
import com.ladrope.venpix.model.Album
import com.ladrope.venpix.model.Moment
import com.ladrope.venpix.services.removeAlbum
import com.ladrope.venpix.services.shareAlbum
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.album_row.view.*


/**
 * Created by USER on 1/9/18.
 */



class AlbumAdapter(options: FirebaseRecyclerOptions<Album>, private val context: Context): FirebaseRecyclerAdapter<Album, AlbumAdapter.ViewHolder>(options){


    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Album) {
        holder.bindItem(model)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.album_row, parent, false)
            return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
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
            val albumImage = FirebaseDatabase.getInstance().reference.child("albums").child(album.albumKey).child("moments")
            var imageUrl: String? = null

            albumImage.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val moment = snapshot.children.iterator().next().getValue(Moment::class.java)
                        imageUrl = moment?.url
                        Log.e("image", imageUrl)
                        Picasso.with(context).load(imageUrl).placeholder(R.drawable.profile_img).into(albumLayout)
                    } catch (e: Throwable) {
                        Log.e("Image", "Error retrieving image url")
                        Picasso.with(context).load(imageUrl).placeholder(R.drawable.profile_img).into(albumLayout)
                    }

                }

                override fun onCancelled(error: DatabaseError) {}
            })

            itemView.setOnClickListener {

                Log.e("Album", album.albumKey)

                var momentIntent = Intent(context, MyMoments::class.java)
                momentIntent.putExtra("albumTitle", albumTitle.text.toString())
                momentIntent.putExtra("albumKey", album.albumKey)
                momentIntent.putExtra("albumCreator", album.creatorId)
                context.startActivity(momentIntent)
            }

            itemView.albumDelete.setOnClickListener {
                removeAlbum(FirebaseAuth.getInstance().uid, album.albumKey, context)
            }

            itemView.albumShare.setOnClickListener {
                shareAlbum(album, context)
            }

        }
    }

}
