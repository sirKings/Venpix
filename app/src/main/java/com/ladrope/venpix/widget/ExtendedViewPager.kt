package com.ladrope.venpix.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.hiteshsahu.awesome_gallery.view.widget.TouchImageView


/**
 * Created by USER on 1/16/18.
 */
class ExtendedViewPager : ViewPager {



    constructor(context: Context) : super(context){
        init()
    }

    var mGesture: GestureDetector? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    override fun canScroll(v: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        return if (v is TouchImageView) {
            //
            // canScrollHorizontally is not supported for Api < 14. To get around this issue,
            // ViewPager is extended and canScrollHorizontallyFroyo, a wrapper around
            // canScrollHorizontally supporting Api >= 8, is called.
            //
            (v).canScrollHorizontally(-dx)

        } else {
            super.canScroll(v, checkV, dx, x, y)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!mGesture!!.onTouchEvent(ev)) {
            super.onInterceptTouchEvent(ev)
            return false
        }

        return true
    }

    fun init() {
        mGesture = GestureDetector(getContext(), object:GestureDetector.SimpleOnGestureListener() {

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                if (distanceX == 0f){
                    return false
                }
                return true
            }

        })
    }


}