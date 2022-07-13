package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils

import android.text.TextUtils
import androidx.room.TypeConverter
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.Tag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TagConverter {
    @TypeConverter
    fun stringToListTag(string: String): List<Tag>? {
        if (TextUtils.isEmpty(string))
            return null
        val type = typeToken<List<Tag>?>()
        return Gson().fromJson(string, type)
    }

    companion object {
        internal inline fun <reified T> String.toObject(): T {
            val type = typeToken<T>()
            return Gson().fromJson(this, type)
        }

        internal inline fun <reified T> typeToken(): Type = object : TypeToken<T>() {}.type

        // Convert a Map to an Object
        internal inline fun <reified T> Map<String, Any>.toObject(): T = convert()
        internal inline fun <T, reified R> T.convert(): R {
            return Gson().toJson(this).toObject()
        }
    }


    @TypeConverter
    fun tagToString(tag: List<Tag>?): String {
        return Gson().toJson(tag)
    }
}