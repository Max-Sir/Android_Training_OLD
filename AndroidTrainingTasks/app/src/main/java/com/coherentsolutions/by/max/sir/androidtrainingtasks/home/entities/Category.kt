package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Category(
    val id: Long,
    val name: String?
) : Parcelable, Serializable {
    override fun toString(): String {
        return "Category(id=$id, name='$name')"
    }
}