package com.example.adjustview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class AdjustScrollView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val paintTube = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }

    private var currentOffsetX = 0F

    private var columnsAmount = 25

    private var columnsOffset = 75

    private var columnsColor = Color.GRAY

    private var columnsWidth = 5

    private var maxLeftOffset = 0F
    private var maxRightOffset = 0F

    private var onScrollCallback: OnScrollCallback? = null

    private var percent: Float = 0F

    init {
        attrs?.let(::initAttrs)

        calculateMaxOffsets()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val percentInt = percent.toInt()
        val absPercent = abs(percentInt)

        for (i in 0..columnsAmount) {
            val absI = abs(i)

            var currentAlpha = 255 - abs(absPercent + i - absPercent * 2) * 50

            paintTube.alpha = currentAlpha

            if (percentInt < 0 && i != 0) {
                val alpha: Int = if (absI < absPercent) {
                    paintTube.alpha - (absPercent * absI - absI) * 50
                } else {
                    paintTube.alpha - (absPercent * 50 * 2)
                }
                if (alpha < 0) {
                    paintTube.alpha = 0
                } else {
                    paintTube.alpha = alpha
                }
            }
            canvas.drawRect(
                    width / 2F + (i * (columnsOffset + columnsWidth)) - (columnsWidth / 2) + currentOffsetX,
                    height / 2F,
                    width / 2F + (i * (columnsOffset + columnsWidth)) + (columnsWidth / 2) + currentOffsetX,
                    height.toFloat(),
                    paintTube
            )

            if (percentInt < 0 && i != 0) {
                paintTube.alpha = currentAlpha
            } else if (percentInt > 0 && i != 0) {
                val alpha: Int = if (absI < absPercent) {
                    paintTube.alpha - (absPercent * absI - absI) * 50
                } else {
                    paintTube.alpha - (absPercent * 50 * 2)
                }
                if (alpha < 0) {
                    paintTube.alpha = 0
                } else {
                    paintTube.alpha = alpha
                }
            }
            canvas.drawRect(
                    width / 2F + (-i * (columnsOffset + columnsWidth)) - (columnsWidth / 2) + currentOffsetX,
                    height / 2F,
                    width / 2F + (-i * (columnsOffset + columnsWidth)) + (columnsWidth / 2) + currentOffsetX,
                    height.toFloat(),
                    paintTube
            )
        }
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (currentOffsetX in maxLeftOffset..maxRightOffset) {
                currentOffsetX -= distanceX
            }
            if (currentOffsetX < maxLeftOffset) {
                currentOffsetX = maxLeftOffset
            } else if (currentOffsetX > maxRightOffset) {
                currentOffsetX = maxRightOffset
            }

            percent = currentOffsetX / maxLeftOffset * columnsAmount
            onScrollCallback?.onScroll(percent)
            invalidate()
            return true
        }
    })

    private fun calculateMaxOffsets() {
        maxRightOffset = ((columnsAmount + 1) * (columnsOffset + columnsWidth)) + (columnsWidth / 2) - (columnsOffset + columnsWidth * 1.5F)
        maxLeftOffset = -maxRightOffset
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AdjustScrollView)

        columnsAmount = typedArray.getInteger(R.styleable.AdjustScrollView_asv_columns_amount, columnsAmount)
        columnsOffset = typedArray.getDimensionPixelSize(R.styleable.AdjustScrollView_asv_columns_offset, columnsOffset)
        columnsColor = typedArray.getColor(R.styleable.AdjustScrollView_asv_columns_color, columnsColor)
        columnsWidth = typedArray.getDimensionPixelSize(R.styleable.AdjustScrollView_asv_columns_width, columnsWidth)

        typedArray.recycle()
    }

    fun setOnScrollCallback(callback: OnScrollCallback) {
        this.onScrollCallback = callback
    }

    interface OnScrollCallback {
        fun onScroll(percent: Float)
    }
}