package com.ladrope.venpix.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import com.hiteshsahu.awesome_gallery.view.widget.TouchImageView

/**
 * Created by USER on 1/16/18.
 */
class ExtendedViewPager : ViewPager {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

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

}