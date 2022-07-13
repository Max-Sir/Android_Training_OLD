package com.coherentsolutions.by.max.sir.androidtrainingtasks.security

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class CiphertextWrapper(val ciphertext: ByteArray, val initializationVector: ByteArray) :
    Parcelable, Serializable