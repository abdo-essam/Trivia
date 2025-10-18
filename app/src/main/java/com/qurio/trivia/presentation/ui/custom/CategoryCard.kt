package com.qurio.trivia.presentation.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import kotlin.math.max

class CategoryCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var imageBitmap: Bitmap? = null
    private var gradientColor: Int = Color.TRANSPARENT
    private val path = Path()
    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val matrix = Matrix()
    private var gradientShader: LinearGradient? = null

    init {
        setCardBorderWidth(5f)
    }

    fun setCardBorderWidth(dp: Float) {
        strokePaint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
        )
        invalidate()
    }

    fun setCardImage(imageResId: Int) {
        val drawable = ContextCompat.getDrawable(context, imageResId) ?: return
        imageBitmap = drawableToBitmap(drawable)
        setupShader()
        invalidate()
    }

    fun setCardGradientColor(colorResId: Int) {
        gradientColor = ContextCompat.getColor(context, colorResId)
        setupGradient()
        invalidate()
    }

    fun setCardBorderColor(colorResId: Int) {
        strokePaint.color = ContextCompat.getColor(context, colorResId)
        invalidate()
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        return createBitmap(
            drawable.intrinsicWidth.coerceAtLeast(1),
            drawable.intrinsicHeight.coerceAtLeast(1)
        ).apply {
            val canvas = Canvas(this)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupPath(w.toFloat(), h.toFloat())
        setupShader()
        setupGradient()
    }

    private fun setupPath(w: Float, h: Float) {
        val topCut = 70f
        val bottomCut = 40f
        val inset = w * 0.05f

        path.apply {
            reset()
            moveTo(topCut, 0f)
            lineTo(w - topCut, 0f)
            lineTo(w, topCut)
            lineTo(w - inset, h - bottomCut)
            lineTo(w - inset - bottomCut, h)
            lineTo(inset + bottomCut, h)
            lineTo(inset, h - bottomCut)
            lineTo(0f, topCut)
            close()
        }
    }

    private fun setupShader() {
        imageBitmap?.let { bitmap ->
            val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            val vw = width.toFloat()
            val vh = height.toFloat()
            val bw = bitmap.width.toFloat()
            val bh = bitmap.height.toFloat()

            matrix.reset()
            val scale = max(vw / bw, vh / bh)
            matrix.setScale(scale, scale)
            matrix.postTranslate((vw - bw * scale) / 2, (vh - bh * scale) / 2)

            shader.setLocalMatrix(matrix)
            imagePaint.shader = shader
        }
    }

    private fun setupGradient() {
        if (width <= 0 || height <= 0) return

        val gradientHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 107f, resources.displayMetrics
        )

        val startY = height - gradientHeight
        val endY = height.toFloat()

        val transparentColor = Color.argb(0,
            Color.red(gradientColor),
            Color.green(gradientColor),
            Color.blue(gradientColor)
        )

        val semiTransparentColor = Color.argb(204,
            Color.red(gradientColor),
            Color.green(gradientColor),
            Color.blue(gradientColor)
        )

        gradientShader = LinearGradient(
            0f, startY,
            0f, endY,
            intArrayOf(transparentColor, semiTransparentColor),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        gradientPaint.shader = gradientShader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.clipPath(path)

        imageBitmap?.let { canvas.drawBitmap(it, matrix, imagePaint) }

        if (gradientColor != Color.TRANSPARENT) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), gradientPaint)
        }

        canvas.drawPath(path, strokePaint)
    }
}