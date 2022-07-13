package com.coherentsolutions.by.max.sir.androidtrainingtasks.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.INFO_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import com.coherentsolutions.by.max.sir.androidtrainingtasks.databinding.ActivityHomeBinding
import com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence.PetstorePersistence
import com.coherentsolutions.by.max.sir.androidtrainingtasks.regestrationmodule.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    companion object {
        const val TERMS_AND_CONDITIONS = "TERMS_AND_CONDITIONS"
        const val USER = "USER"
        const val USERNAME = "USERNAME"
    }

    @Inject
    lateinit var persistence: PetstorePersistence

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag(INFO_TAG).i("home activity created")

        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)

        Timber.tag(INFO_TAG).i("Home act inflated")
        val bottomNavigationItem = binding.bottomNavigation

        /**TODO IN ANY CAUSE: U CAN DELETE THIS if all will be wrong
         * @note this code restricts recreation of the fragment
         *      if this fragment had been already opened by AppBar click
         *      instead of recreate you should pull to refresh
         *      */
        bottomNavigationItem.setOnItemReselectedListener {
            return@setOnItemReselectedListener
        }
        Timber.tag(INFO_TAG).i("BOTTOM NAV ITEM INSTANTIATED")
        val navController = this.findNavController(R.id.myNavHostFragment)
        Timber.tag(INFO_TAG).i("NAV CONTROLLER GOT")
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.userFragment, R.id.petsFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        Timber.tag(INFO_TAG).i("NAV BAR CONFIGURED")
        bottomNavigationItem.setupWithNavController(navController)

    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout_question))
            .setMessage(getString(R.string.are_you_sure_you_want_to_logout_question))
            .setPositiveButton(getText(R.string.yes)) { dialog, _ ->
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
                persistence.clearCrypto()
                dialog.cancel()
            }
            .setNegativeButton(getText(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }


}