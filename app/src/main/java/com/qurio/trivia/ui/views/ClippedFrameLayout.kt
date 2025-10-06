package com.qurio.trivia.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.graphics.withClip

class ClippedFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val clipPath = Path()

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateClipPath(w.toFloat(), h.toFloat())
    }

    private fun updateClipPath(width: Float, height: Float) {
        if (width == 0f || height == 0f) return

        // Scale factors from viewPort (188x257) to actual view size
        val scaleX = width / 188f
        val scaleY = height / 257f

        clipPath.reset()
        clipPath.apply {
            // Using the exact path from SVG clip-path
            moveTo(172f * scaleX, 33.53f * scaleY)
            lineTo(165.44f * scaleX, 230.7f * scaleY)
            lineTo(157.19f * scaleX, 241f * scaleY)
            lineTo(30.96f * scaleX, 241f * scaleY)
            lineTo(21.97f * scaleX, 231.42f * scaleY)
            lineTo(16f * scaleX, 37.02f * scaleY)
            lineTo(30.49f * scaleX, 16f * scaleY)
            lineTo(155.26f * scaleX, 16f * scaleY)
            close()
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (!clipPath.isEmpty) {
            canvas.withClip(clipPath) {
                super.dispatchDraw(this)
            }
        } else {
            super.dispatchDraw(canvas)
        }
    }
}