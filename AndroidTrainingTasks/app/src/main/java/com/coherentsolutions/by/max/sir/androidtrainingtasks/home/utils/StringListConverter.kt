package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils

import android.text.TextUtils
import androidx.room.TypeConverter
import com.google.gson.Gson

class StringListConverter {
    @TypeConverter
    fun stringToStringList(string: String): List<String>? {
        if (TextUtils.isEmpty(string))
            return null

        val type = TagConverter.typeToken<List<String>?>()
        return Gson().fromJson(string, type)
    }

    @TypeConverter
    fun stringListToString(photoUrlsList: List<String>?): String {
        return Gson().toJson(photoUrlsList)
    }
}