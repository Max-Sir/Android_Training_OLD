package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils

@Suppress("unused")
class ActivityProgressBar(val progressBar: ActivityIndicator) {

    init {
        progressBar.onIndeterminateModeChangeListener = { progress ->
            if (progress) {
                progressBar.animate()
            }
        }
    }


    fun show() {
        progressBar.show()
    }

    fun hide() {
        progressBar.hide()
    }
}