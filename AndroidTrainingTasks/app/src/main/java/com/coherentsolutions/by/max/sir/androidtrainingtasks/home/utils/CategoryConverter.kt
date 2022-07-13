package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils

import android.text.TextUtils
import androidx.room.TypeConverter
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.Category
import com.google.gson.Gson

class CategoryConverter {
    @TypeConverter
    fun stringToCategory(string: String): Category? {
        if (TextUtils.isEmpty(string))
            return null
        return Gson().fromJson(string, Category::class.java)
    }

    @TypeConverter
    fun categoryToString(category: Category?): String {
        if (category == null)
            return ""
        return Gson().toJson(category)
    }
}