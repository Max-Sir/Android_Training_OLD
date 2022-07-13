package com.coherentsolutions.by.max.sir.androidtrainingtasks.webcontent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import com.coherentsolutions.by.max.sir.androidtrainingtasks.databinding.ActivityTermsAndConditionsBinding
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.HomeActivity
import com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence.PetstorePersistence
import com.coherentsolutions.by.max.sir.androidtrainingtasks.regestrationmodule.ui.login.LoginActivity
import com.coherentsolutions.by.max.sir.androidtrainingtasks.service.persistence
import timber.log.Timber
import java.lang.Float.min

class TermsAndConditionsActivity : AppCompatActivity() {

    fun goFullscreen() {
        with(WindowInsetsControllerCompat(window, window.decorView)) {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
            hide(Type.statusBars() or Type.navigationBars())
        }
    }

    fun exitFullscreen() {
        WindowInsetsControllerCompat(window, window.decorView).show(Type.systemBars())
    }

    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goFullscreen()
        val binding = DataBindingUtil.setContentView<ActivityTermsAndConditionsBinding>(
            this,
            R.layout.activity_terms_and_conditions
        )
        binding.apply {
            buttonAcceptAndContinue.setOnClickListener {
                persistence<PetstorePersistence>().markTermsAndConditionsAsReadedAndAccepted()
                startActivity(Intent(this@TermsAndConditionsActivity, HomeActivity::class.java))
            }

            termsAndConditions.apply {
                loadUrl("https://www.privacypolicies.com/blog/sample-terms-conditions-template")
                webViewClient = WebViewClient()
                setOnScrollChangeListener(object : View.OnScrollChangeListener {
                    override fun onScrollChange(
                        view: View?,
                        scrollX: Int,
                        scrollY: Int,
                        oldScrollX: Int,
                        oldScrollY: Int
                    ) {
                        val webView = view as WebView
                        val contentHeight = webView.getContentHeight() * webView.getScaleY()
                        val total =
                            contentHeight * getResources().getDisplayMetrics().density - view.getHeight()
                        val percent =
                            min(scrollY / (total - getResources().getDisplayMetrics().density), 1f)
                        Timber.tag("SCROLL").d("Percentage: $percent")
                        if (scrollY >= total - 1) {
                            Timber.tag("SCROLL").d("Reached bottom")
                        }
                        if (percent in listOf(0.9999935f, 1f)) {
                            binding.buttonAcceptAndContinue.visibility = View.VISIBLE
                        } else {
                            binding.buttonAcceptAndContinue.visibility = View.GONE
                        }
                    }
                })
            }
        }
    }
}