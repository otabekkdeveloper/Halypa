package com.example.arkadagapp.presentation.settings

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.arkadagapp.R

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress: Int = 0

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.app_color)
        style = Paint.Style.FILL
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.blue)
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.blue)
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val rect = RectF()

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = minOf(width, height) / 2 - borderPaint.strokeWidth / 2 // Uchest' border

        val centerX = width / 2
        val centerY = height / 2

        rect.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        if (progress > 0) {
            val sweepAngle = (progress / 100f) * 360f
            canvas.drawArc(rect, -90f, sweepAngle, true, progressPaint)
        }

        canvas.drawCircle(centerX, centerY, radius, borderPaint)
    }
}