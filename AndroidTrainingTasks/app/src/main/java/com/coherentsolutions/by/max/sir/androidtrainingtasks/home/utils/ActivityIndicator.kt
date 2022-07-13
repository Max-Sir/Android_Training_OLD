package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R

class ActivityIndicator(context: Context, attributeSet: AttributeSet) :
    ProgressBar(context, attributeSet) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }


    var onIndeterminateModeChangeListener: ((Boolean) -> Unit)? = null
    var viewPropertyAnimator: ViewPropertyAnimator? = null

    @Suppress("unused")
    var indeterminateMode = false
        set(value) {
            field = value
            onIndeterminateModeChangeListener?.invoke(field)
        }

    fun show() {
        this.visibility = View.VISIBLE
        this.indeterminateDrawable = getDrawable(context, R.drawable.loading_animation)
        viewPropertyAnimator = this.animate()
    }

    fun hide() {
        this.visibility = View.GONE
        viewPropertyAnimator?.cancel()
    }


}