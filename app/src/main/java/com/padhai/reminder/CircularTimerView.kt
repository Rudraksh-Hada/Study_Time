package com.padhai.reminder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/** A full-circle arc that shrinks as the timer ticks down. */
class CircularTimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 18f
        color = 0x33FFFFFF  // dim white track
        strokeCap = Paint.Cap.ROUND
    }

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 18f
        color = 0xFFFF3D9A.toInt()  // berry pink
        strokeCap = Paint.Cap.ROUND
    }

    private val oval = RectF()

    /** 0f = empty, 1f = full */
    var fraction: Float = 1f
        set(v) { field = v.coerceIn(0f, 1f); invalidate() }

    var accentColor: Int = 0xFFFF3D9A.toInt()
        set(v) { field = v; arcPaint.color = v; invalidate() }

    override fun onDraw(canvas: Canvas) {
        val pad = trackPaint.strokeWidth / 2f + 4f
        oval.set(pad, pad, width - pad, height - pad)

        // full track
        canvas.drawArc(oval, -90f, 360f, false, trackPaint)
        // progress arc — sweeps counter-clockwise as time runs out
        canvas.drawArc(oval, -90f, 360f * fraction, false, arcPaint)
    }
}
