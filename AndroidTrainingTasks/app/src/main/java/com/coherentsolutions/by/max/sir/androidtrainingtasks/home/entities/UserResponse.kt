package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class UserResponse(
    val email: String,
    val firstName: String,
    val id: Int,
    val lastName: String,
    val password: String,
    val phone: String,
    val userStatus: Int,
    val username: String
) : Serializable, Parcelable {
    companion object {
        fun createEmptyUserToHold(username: String) =
            UserResponse("", "", -1, "", "", "", -1, username)
    }
}