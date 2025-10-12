package com.qurio.trivia.presentation.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class StrokeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var strokeWidth = 1f
    private var strokeColor = 0xFFFFFFFF.toInt()

    init {
        // Set stroke properties
        strokeWidth = 1f * resources.displayMetrics.density
    }

    override fun onDraw(canvas: Canvas) {
        val textColor = currentTextColor

        // Draw stroke
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        setTextColor(strokeColor)
        super.onDraw(canvas)

        // Draw fill
        paint.style = Paint.Style.FILL
        setTextColor(textColor)
        super.onDraw(canvas)
    }
}