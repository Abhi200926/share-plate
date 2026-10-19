package com.example.data.local.converters

import androidx.room.TypeConverter

class SharePlateTypeConverters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        if (list == null) return null
        return list.joinToString(separator = "|||")
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrEmpty()) return emptyList()
        return data.split("|||")
    }
}
