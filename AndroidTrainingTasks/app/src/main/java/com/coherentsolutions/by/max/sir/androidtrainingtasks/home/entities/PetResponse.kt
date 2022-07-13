package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import javax.inject.Inject

@Parcelize
data class PetResponse @Inject constructor(
    val category: Category?,
    val id: Long,
    val name: String?,
    val photoUrls: List<String>?,
    val status: String,
    val tags: List<Tag>?
) : Parcelable, Serializable {
    override fun toString(): String {
        return "PetResponse(category=$category, id=$id, name='$name', photoUrls=$photoUrls, status='$status', tags=$tags)"
    }

    fun toPet() = Pet(category, id, name, photoUrls, status, tags)
}