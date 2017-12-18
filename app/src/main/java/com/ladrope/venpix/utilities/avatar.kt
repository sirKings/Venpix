package com.ladrope.venpix.utilities

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable

/**
 * Created by USER on 12/18/17.
 */

class LetterAvatar(context: Context, color:Int, letter:Char?, paddingInDp:Int): ColorDrawable(color) {
    internal var paint = Paint()
    internal var bounds = Rect()
    internal var pLetters:String
    private var ONE_DP = 0.0f
    private val pResources: Resources
    private var pPadding:Int = 0
    internal var pSize = 0f
    internal var pMesuredTextWidth:Float = 0.toFloat()
    internal var pBoundsTextwidth:Int = 0
    internal var pBoundsTextHeight:Int = 0
    init{
        this.pLetters = letter.toString()
        this.pResources = context.getResources()
        ONE_DP = 1 * pResources.getDisplayMetrics().density
        this.pPadding = Math.round(paddingInDp * ONE_DP)
    }
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        paint.setAntiAlias(true)
        do
        {
            paint.setTextSize(++pSize)
            paint.getTextBounds(pLetters, 0, pLetters.length, bounds)
        }
        while ((bounds.height() < (canvas.getHeight() - pPadding)) && (paint.measureText(pLetters) < (canvas.getWidth() - pPadding)))
        paint.setTextSize(pSize)
        pMesuredTextWidth = paint.measureText(pLetters)
        pBoundsTextHeight = bounds.height()
        val xOffset = ((canvas.getWidth() - pMesuredTextWidth) / 2)
        val yOffset = ((pBoundsTextHeight + (canvas.getHeight() - pBoundsTextHeight) / 2) as Int).toFloat()
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
        paint.setColor(-0x1)
        canvas.drawText(pLetters, xOffset, yOffset, paint)
    }
}

