package com.ladrope.venpix.Adapters

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hiteshsahu.awesome_gallery.view.widget.TouchImageView
import com.ladrope.venpix.model.Moment
import com.squareup.picasso.Picasso



/**
 * Created by USER on 1/16/18.
 */
class FullScreenAdapter(private var list: ArrayList<Moment>) : PagerAdapter() {

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return list.size
    }


    override fun getItemPosition(`object`: Any): Int {
        //return super.getItemPosition(`object`)
        when (list.contains(`object`)){
            true -> return list.indexOf(`object`)
            false-> return POSITION_NONE
        }

    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val touchImageView = TouchImageView(container.context)

        //load image into touchImageView
        Picasso.with(container.context).load(list[position].url).into(touchImageView)

        container.addView(touchImageView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)

        return touchImageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun deleteMoment(position: Int){
        list.removeAt(position)
        notifyDataSetChanged()
    }
}