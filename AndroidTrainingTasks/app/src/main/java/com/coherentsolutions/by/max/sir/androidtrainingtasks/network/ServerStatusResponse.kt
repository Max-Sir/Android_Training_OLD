package com.coherentsolutions.by.max.sir.androidtrainingtasks.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServerStatusResponse(
    val code: Int,
    val message: String,
    val type: String
) : Parcelable