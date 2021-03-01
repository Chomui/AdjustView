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

    private var currentOffsetX = 0F

    private var alphaOffset = 2

    var columnsAmount = 25
        private set

    private var columnsOffset = 75F

    private var columnsColor = Color.GRAY
    private var dotColor = Color.GRAY

    private val columnsPaint = Paint().apply {
        isAntiAlias = true
        color = columnsColor
    }

    private val dotPaint = Paint().apply {
        isAntiAlias = true
        color = dotColor
    }

    private var columnsWidth = 5F
    private var dotRadius = 5F

    private var maxLeftOffset = 0F
    private var maxRightOffset = 0F

    private var onScrollCallback: OnScrollCallback? = null

    private var progress: Float = 0F

    init {
        attrs?.let(::initAttrs)

        calculateMaxOffsets()

        columnsPaint.color = columnsColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return

        // dot
        canvas.drawCircle(width / 2F, dotRadius, dotRadius, dotPaint)

        // columns
        val progressInt = progress.toInt()
        val absProgress = abs(progressInt)

        for (i in 0..columnsAmount) {
            val rightColumnPos = width / 2F + (i * (columnsOffset + columnsWidth)) + currentOffsetX
            calculateColumnsAlpha(rightColumnPos)
            canvas.drawRect(
                rightColumnPos - (columnsWidth / 2),
                height / 3F,
                rightColumnPos + (columnsWidth / 2),
                height.toFloat(),
                columnsPaint
            )

            val leftColumnPos = width / 2F + (-i * (columnsOffset + columnsWidth)) + currentOffsetX
            calculateColumnsAlpha(leftColumnPos)
            canvas.drawRect(
                leftColumnPos - (columnsWidth / 2),
                height / 3F,
                leftColumnPos + (columnsWidth / 2),
                height.toFloat(),
                columnsPaint
            )
        }
    }

    private fun calculateColumnsAlpha(position: Float) {
        if (position > width || position < 0) {
            columnsPaint.alpha = 0
        } else {
            var alpha = 255 - abs(((width / 2) - abs(position).toInt())) / alphaOffset

            if (alpha > 255) {
                alpha = 255
            } else if (alpha < 0){
                alpha = 0
            }

            columnsPaint.alpha = alpha
        }
    }

    fun setProgress(progress: Float) {
        if (this.progress != progress) {
            this.progress = progress
            currentOffsetX = progress / columnsAmount * maxLeftOffset
            invalidate()
        }
    }

    fun setColumnsAmount(amount: Int) {
        if (this.columnsAmount != amount) {
            this.columnsAmount = amount
            calculateMaxOffsets()
            setProgress(0F)
        }
    }

    fun setColumnsOffset(offset: Float) {
        if (this.columnsOffset != offset) {
            this.columnsOffset = offset
            calculateMaxOffsets()
            setProgress(0F)
        }
    }

    fun setColumnsColor(color: Int) {
        if (this.columnsColor != color) {
            this.columnsColor = color
            columnsPaint.color = color
            invalidate()
        }
    }

    fun setDotColor(color: Int) {
        if (this.dotColor != color) {
            this.dotColor = color
            dotPaint.color = color
            invalidate()
        }
    }

    fun setColumnsWidth(width: Float) {
        if (this.columnsWidth != width) {
            this.columnsWidth = width
            calculateMaxOffsets()
            setProgress(0F)
        }
    }

    fun setDotRadius(radius: Float) {
        if (this.dotRadius != radius) {
            this.dotRadius = radius
            invalidate()
        }
    }

    fun setOnScrollCallback(callback: OnScrollCallback) {
        this.onScrollCallback = callback
    }

    interface OnScrollCallback {
        fun onScroll(percent: Float)
    }

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (currentOffsetX in maxLeftOffset..maxRightOffset) {
                    currentOffsetX -= distanceX
                }
                if (currentOffsetX < maxLeftOffset) {
                    currentOffsetX = maxLeftOffset
                } else if (currentOffsetX > maxRightOffset) {
                    currentOffsetX = maxRightOffset
                }

                progress = currentOffsetX / maxLeftOffset * columnsAmount
                onScrollCallback?.onScroll(progress)
                invalidate()
                return true
            }
        })

    private fun calculateMaxOffsets() {
        maxRightOffset =
            ((columnsAmount + 1) * (columnsOffset + columnsWidth)) + (columnsWidth / 2) - (columnsOffset + columnsWidth * 1.5F)
        maxLeftOffset = -maxRightOffset
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AdjustScrollView)

        columnsAmount =
            typedArray.getInteger(R.styleable.AdjustScrollView_asv_columns_amount, columnsAmount)
        columnsOffset =
            typedArray.getDimension(R.styleable.AdjustScrollView_asv_columns_offset, columnsOffset)
        columnsColor =
            typedArray.getColor(R.styleable.AdjustScrollView_asv_columns_color, columnsColor)
        dotColor = typedArray.getColor(R.styleable.AdjustScrollView_asv_dot_color, dotColor)
        columnsWidth =
            typedArray.getDimension(R.styleable.AdjustScrollView_asv_columns_width, columnsWidth)
        dotRadius = typedArray.getDimension(R.styleable.AdjustScrollView_asv_dot_radius, dotRadius)

        typedArray.recycle()
    }
}