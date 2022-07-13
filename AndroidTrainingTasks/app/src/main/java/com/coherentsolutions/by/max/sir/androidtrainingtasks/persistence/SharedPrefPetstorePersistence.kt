package com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.HomeActivity.Companion.TERMS_AND_CONDITIONS
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.HomeActivity.Companion.USER
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.UserResponse
import com.coherentsolutions.by.max.sir.androidtrainingtasks.security.BiometricPromptUtils
import com.google.gson.Gson
import timber.log.Timber
import javax.inject.Inject

/**Now deprecated and unneeded operation to pass user from Activity to Fragment
 *  now responsibility to do this is on DB and to pass username to perform get operations is lye on
 *  intent put/get Extra(String)
 *
 *  TODO("You may need use Shared Preferences in the future versions so left this code there to
 *                              easily rebase it instead involve the same code from the beggining")
 *  @since 27-Sep-2021
 *  @author Maxim Syromolotov
 */
class SharedPrefPetstorePersistence @Inject constructor(val context: Context) :
    PetstorePersistence {


    init {
        Timber.tag(MyApplication.INFO_TAG).i("User persistence instantiated")
    }

    override fun markTermsAndConditionsAsReadedAndAccepted(accepted: Boolean) {
        val preferences = context.getSharedPreferences(TERMS_AND_CONDITIONS, MODE_PRIVATE)
        val edit = preferences.edit()
        edit.putBoolean(TERMS_AND_CONDITIONS, accepted).apply()
        Timber.tag(MyApplication.INFO_TAG).i("Terms were accepted and saved in persistence")
    }

    override fun revokeTermsAndConditionsState() {
        if (!isTermsConditionsAccepted()) {
            Timber.tag(MyApplication.INFO_TAG).i("terms were already not accepted yet")
            return
        }
        markTermsAndConditionsAsReadedAndAccepted(accepted = false)
    }

    override fun isTermsConditionsAccepted(): Boolean {
        val preferences = context.getSharedPreferences(TERMS_AND_CONDITIONS, MODE_PRIVATE)
        Timber.tag(MyApplication.INFO_TAG).i("terms were checked for acceptance")
        return preferences.getBoolean(TERMS_AND_CONDITIONS, false)
    }

    override fun loadUser(): UserResponse? {

        val preferences = context.getSharedPreferences(USER, MODE_PRIVATE)
        val userJson = preferences.getString(USER, null) ?: return null
        val userResponse: UserResponse =
            Gson().fromJson(
                userJson,
                UserResponse::class.java
            ) as UserResponse
        Timber.tag(MyApplication.INFO_TAG).i("User loaded")

        return userResponse
    }

    override fun clearCurrentUser() =
        context.getSharedPreferences(USER, MODE_PRIVATE).edit().clear().apply()

    override fun clearCrypto() =
        context.getSharedPreferences(BiometricPromptUtils.SHARED_PREFS_FILENAME, MODE_PRIVATE)
            .edit().clear().apply()

    override fun getCrypto(): Boolean {
        context.getSharedPreferences(BiometricPromptUtils.SHARED_PREFS_FILENAME, MODE_PRIVATE)
            .getString(BiometricPromptUtils.CIPHERTEXT_WRAPPER, null) ?: return false
        return true

    }

    override fun saveUser(userResponse: UserResponse) {
        val preferences = context.getSharedPreferences(USER, MODE_PRIVATE)
        val edit = preferences.edit()
        val gson = Gson().toJson(userResponse)
        edit.putString(USER, gson).apply()
        Timber.tag(MyApplication.INFO_TAG).i("User saved in persistence")
    }
}