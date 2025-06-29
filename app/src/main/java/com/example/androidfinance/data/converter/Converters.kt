package com.example.androidfinance.data.converter

import androidx.room.TypeConverter
import com.example.androidfinance.data.entity.RecordType
import java.util.Date

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromRecordType(type: RecordType): String {
        return type.name
    }

    @TypeConverter
    fun toRecordType(type: String): RecordType {
        return RecordType.valueOf(type)
    }
}
