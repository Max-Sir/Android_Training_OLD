package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@kotlinx.serialization.Serializable
@Parcelize
data class Tag(
    val id: Long,
    val name: String
) : Parcelable, Serializable {
    override fun toString(): String {
        return "Tag(id=$id, name='$name')"
    }
}