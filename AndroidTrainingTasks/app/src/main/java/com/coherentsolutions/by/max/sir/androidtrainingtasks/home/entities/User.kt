package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "users_table")
data class User(
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "last_name")
    val lastName: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "phone")
    val phone: String,
    @ColumnInfo(name = "user_status")
    val userStatus: Int,
    @ColumnInfo(name = "username")
    val username: String,
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0L
) : Serializable, Parcelable