package com.example.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class CustomView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val top = 200f
    private val left = 200f

    private val TAG = "viewlogger"
    private val mainRect = RectF(left, top, left + 300f, top + 200f)
    private var mainRectEvent = RectF(mainRect)

    private val rotateRect = RectF(left, top, left + 80f, top + 80f)
    private var rotateRectEvent = RectF(rotateRect)
    private var rotateRectDraw = RectF(rotateRect)

    private val scaleRect = RectF(left, top + 120f, left + 80f, top + 200f)
    private var scaleRectEvent = RectF(scaleRect)
    private var scaleRectDraw = RectF(scaleRect)

    private var rotate = 0f
    private var scale = 1f
    private var translateX = 0f
    private var translateY = 0f

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        scaleRectDraw.set(
            scaleRect.left,
            scaleRect.bottom - scaleRect.height() / scale,
            scaleRect.left + scaleRect.width() / scale,
            scaleRect.bottom
        )
        rotateRectDraw.set(
            rotateRect.left,
            rotateRect.top,
            rotateRect.left + rotateRect.width() / scale,
            rotateRect.top + rotateRect.height() / scale
        )


        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint.color = Color.RED
        canvas.drawRect(mainRectEvent, paint)
        paint.color = Color.BLACK
        canvas.save()
        canvas.translate(translateX, translateY)
        canvas.rotate(rotate, mainRect.centerX(), mainRect.centerY())
        canvas.scale(scale, scale, mainRect.centerX(), mainRect.centerY())
        paint.color = Color.parseColor("#2196F3")
        canvas.drawRect(mainRect, paint)
        paint.color = Color.parseColor("#9C27B0")
        canvas.drawRect(rotateRectDraw, paint)
        paint.color = Color.parseColor("#000000")
        canvas.drawRect(scaleRectDraw, paint)
        canvas.restore()
        canvas.drawCircle(mainRectEvent.centerX(), mainRectEvent.centerY(),10f,  paint)
    }

    private var isOnRotate = false
    private var isOnScale = false
    private var isOnFinger = false
    private var lastX = 0f
    private var lastY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val x = event.x
        val y = event.y
        Log.d(TAG, "onTouchEvent: $x, $y")
        Log.d(TAG, "onTouchEvent_last: $lastX, $lastY")

        scaleRectEvent = RectF(scaleRect)
        rotateRectEvent = RectF(rotateRect)
        mainRectEvent = RectF(mainRect)

        val matrix = Matrix()
        matrix.postScale(scale, scale, mainRect.centerX(), mainRect.centerY())
        matrix.postRotate(rotate, mainRect.centerX(), mainRect.centerY())
        matrix.postTranslate(translateX, translateY)
        matrix.mapRect(scaleRectEvent)
        matrix.mapRect(rotateRectEvent)
        matrix.mapRect(mainRectEvent)

        if (action == MotionEvent.ACTION_UP) {
            isOnRotate = false
            isOnScale = false
            isOnFinger = false
        }
        if (isOnRotate) {
            rotate(x, y)
        }
        if (isOnScale) {
            scale(x, y)
        }
        if (isOnFinger) {
            translate(x, y)
        }
        if (rotateRectEvent.contains(x, y) && action == MotionEvent.ACTION_DOWN) {
            isOnRotate = true
        }
        if (scaleRectEvent.contains(x, y) && action == MotionEvent.ACTION_DOWN) {
            isOnScale = true
        }
        if (!isOnScale && !isOnRotate && mainRectEvent.contains(x, y) && action == MotionEvent.ACTION_DOWN) {
            isOnFinger = true
        }
        lastX = x
        lastY = y

        return true
    }

    private fun translate(x: Float, y: Float) {
        Log.d(TAG, "translate: $translateX, $translateY")
        translateX += x - lastX
        translateY += y - lastY
        postInvalidate()
    }

    private fun scale(x: Float, y: Float) {
        val scale = sqrt(
            ((x - mainRectEvent.centerX()).pow(2) + (y - mainRectEvent.centerY()).pow(2))
        ) / sqrt(
            (lastX - mainRectEvent.centerX()).pow(2) + (lastY - mainRectEvent.centerY()).pow(
                2
            )
        )
        this.scale *= scale
        Log.d(TAG, "scale: ${this.scale}")
        postInvalidate()
    }

    private fun rotate(x: Float, y: Float) {
        val rotate = (Math.toDegrees(
            atan2(
                y.toDouble() - mainRectEvent.centerY(),
                x.toDouble() - mainRectEvent.centerX()
            )
        ) -
                Math.toDegrees(
                    atan2(
                        lastY - mainRectEvent.centerY(),
                        lastX - mainRectEvent.centerX()
                    ).toDouble()
                )).toFloat()
        this.rotate += rotate
        Log.d(TAG, "rotate:${this.rotate}")
        postInvalidate()
    }
}