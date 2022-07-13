package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils.CategoryConverter
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils.StringListConverter
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.utils.TagConverter

@Entity(tableName = "pets_table")
data class Pet(
    @ColumnInfo(name = "category")
    @TypeConverters(CategoryConverter::class)
    val category: Category?,
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "photoUrls")
    @TypeConverters(StringListConverter::class)
    val photoUrls: List<String>?,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "tags")
    @TypeConverters(TagConverter::class)
    val tags: List<Tag>?,
    @PrimaryKey(autoGenerate = true)
    val petId: Long = 0L
) {
    fun toPetResponse() = PetResponse(category, id, name, photoUrls, status, tags)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pet

        if (category != other.category) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (photoUrls != other.photoUrls) return false
        if (status != other.status) return false
        if (tags != other.tags) return false

        return true
    }
}