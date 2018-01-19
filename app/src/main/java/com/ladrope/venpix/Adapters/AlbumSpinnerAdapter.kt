package com.ladrope.venpix.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Album



/**
 * Created by USER on 1/17/18.
 */
class AlbumSpinnerAdapter( private var list: ArrayList<Album>, private val context: Context): BaseAdapter(){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.spinner_album_row, parent,false)

        val albumTitle = view.findViewById<TextView>(R.id.spinnerRowAlbumTitle)
        val albumDesc = view.findViewById<TextView>(R.id.spinnerRowAlbumDesc)
        val albumCreator = view.findViewById<TextView>(R.id.spinnerRowAlbumCreator)

        albumTitle.text = list[position].albumTitle
        albumCreator.text = list[position].creatorName
        albumDesc.text = list[position].albumDesc

        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return list.size
    }

}