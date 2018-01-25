package com.ladrope.venpix.Adapters

import android.content.Context
import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import com.hiteshsahu.awesome_gallery.view.widget.TouchImageView
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Moment
import com.ladrope.venpix.services.saveImage
import com.squareup.picasso.Picasso


/**
 * Created by USER on 1/16/18.
 */
class FullScreenAdapter(private var list: ArrayList<Moment>, private val context: Context) : PagerAdapter() {

    var mCurrentView: View? = null
    var mTag: String? = null

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

        if(list[position].type == "VIDEO"){
            val videoView = VideoView(container.context)
            val vidUri = Uri.parse(list[position].url)
            videoView.setVideoURI(vidUri)
            val control = MediaController(container.context)
            control.setAnchorView(videoView)
            videoView.setMediaController(control)
            videoView.start()
            container.addView(videoView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            return videoView
        }else{
            val touchImageView = TouchImageView(container.context)

            //load image into touchImageView
            Picasso.with(container.context).load(list[position].url).into(touchImageView)

            val gdt = GestureDetector(GestureListener())

            touchImageView.setOnTouchListener { view, event ->
                gdt.onTouchEvent(event)
                true
            }


            container.addView(touchImageView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)

            return touchImageView
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun deleteMoment(position: Int){
        list.removeAt(position)
        notifyDataSetChanged()
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
        mCurrentView = `object` as View
        mTag = "TAG"
    }

    private inner class GestureListener : SimpleOnGestureListener() {

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (distanceX == 0f){

                if (e2!!.y > e1!!.y) {

                    if (mTag != null){
                        Log.e("Motion", "Ended")
                        saveImage(mCurrentView, context)
                        Log.e("currentItem", mCurrentView.toString())
                        mTag = null
                    }else{
                        Toast.makeText(context, context.getString(R.string.imageSaved), Toast.LENGTH_SHORT).show()
                    }
                }else {
                    // direction up

                }
            }
            return true
        }
    }
}