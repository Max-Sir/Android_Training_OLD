package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import kotlinx.android.synthetic.main.custom_ui_view_layout.view.*

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    var onButtonSelectedChangeListener: ((Boolean) -> Unit)? = null


    init {
        init(attrs)
        onButtonSelectedChangeListener = {
            when (this.isButtonSelected) {
                true -> {
                    this.custom_label_text.text = context.getString(R.string.highlighted)
                    this.custom_button.setBackgroundColor(context.getColor(R.color.teal_200))
                }
                else -> {
                    this.custom_label_text.text = context.getString(R.string.normal_text)
                    this.custom_button.setBackgroundColor(context.getColor(R.color.purple_500))
                }
            }
            this.invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    var isButtonSelected = false
        set(value) {
            field = value
            onButtonSelectedChangeListener?.invoke(field)
        }


    @SuppressLint("ClickableViewAccessibility")
    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.custom_ui_view_layout, this)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.CustomView)
        try {
            this.custom_label_text.text = context.getString(R.string.normal_text)
        } finally {
            custom_button.setOnTouchListener { view, motionEvent ->
                //this.isButtonSelected=custom_button.isSelected
                if (motionEvent.action == 0) {
                    isButtonSelected = true
                    this.invalidate()
                } else {
                    isButtonSelected = false
                    this.invalidate()
                }

                return@setOnTouchListener false
            }
            ta.recycle()
        }
    }
}