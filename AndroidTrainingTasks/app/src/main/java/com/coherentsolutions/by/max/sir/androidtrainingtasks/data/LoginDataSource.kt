package com.coherentsolutions.by.max.sir.androidtrainingtasks.data

import android.util.Log
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.INFO_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */

@Suppress("UNUSED_PARAMETER")
class LoginDataSource {

    fun login(
        username: String,
        password: String,
        email: String,
        phone: String,
        firstname: String,
        lastname: String
    ): Result<LoggedInUser> {
        try {
            Log.i(INFO_TAG, "login method")
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        Log.i(INFO_TAG, "logout()")
        // TODO: revoke authentication
    }
}
