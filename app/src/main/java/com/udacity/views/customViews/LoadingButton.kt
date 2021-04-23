package com.udacity.views.customViews

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import com.udacity.R

import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor( context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var brush = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pen = Paint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var valueAnimator : ValueAnimator

    private var radiusShape = 0f
    private var fillShape = 0f

    private var buttonText = context.getString(R.string.button_name)
    // Save text strings in order not to have to search for them constantly.
    private var buttonTextDefault = context.getString(R.string.button_name)
    private var buttonTextLoading = context.getString(R.string.button_loading)

    private lateinit var rectArc : RectF

    private var colorsPalette : MutableMap<String, Int> = mutableMapOf()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when(new){
            ButtonState.Loading -> {
                buttonText = buttonTextLoading
                valueAnimator.start()

            }
            ButtonState.Completed -> {
                buttonText = buttonTextDefault
                valueAnimator.end()
                radiusShape = 0f
                fillShape = 0f
                invalidate()
            }
        }

        valueAnimator.addUpdateListener { dynamicValue ->
            val dynamicValueFloat = dynamicValue.animatedValue as Float

            fillShape = dynamicValueFloat
            radiusShape = (dynamicValueFloat * 360) / widthSize

            invalidate()
        }
    }

    init {
        isClickable = true

        val retrievedAttributes = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)
        colorsPalette["Background"] = ResourcesCompat.getColor(resources,
            retrievedAttributes.getResourceId(R.styleable.LoadingButton_backgroundColor, R.color.colorPrimary),
            null)
        colorsPalette["Loading bar"] = ResourcesCompat.getColor(resources,
            retrievedAttributes.getResourceId(R.styleable.LoadingButton_loadingBarColor, R.color.colorPrimaryDark),
            null)
        colorsPalette["Circle"] = ResourcesCompat.getColor(resources,
            retrievedAttributes.getResourceId(R.styleable.LoadingButton_circleColor, R.color.colorAccent),
            null)
        colorsPalette["Text"] = ResourcesCompat.getColor(resources,
            retrievedAttributes.getResourceId(R.styleable.LoadingButton_textColor, R.color.white),
            null)

        retrievedAttributes.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        // Background
        canvas?.drawColor(colorsPalette["Background"]!!)

        // Loading bar
        brush.color = colorsPalette["Loading bar"]!!
        canvas?.drawRect(0f, 0f, fillShape, measuredHeight.toFloat(), brush)

        // Text
        pen.color = colorsPalette["Text"]!!
        pen.textSize = 80f
        pen.textAlign = Paint.Align.CENTER
        canvas?.drawText(buttonText, measuredWidth/2f,
            ((measuredHeight/2f) - ((pen.descent() + pen.ascent()) / 2)),
            pen)

        // Circle
        brush.color = colorsPalette["Circle"]!!
        canvas?.drawArc(rectArc, 0f, fillShape, true, brush)

        canvas?.save()
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        widthSize = w
        heightSize = h

        rectArc = RectF(widthSize/4f * 3, heightSize/2f - 50,
            100 + (widthSize/4f * 3), heightSize/2f + 50)

        valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply{
            duration = 2000
            interpolator = LinearInterpolator()
        }
    }
}

