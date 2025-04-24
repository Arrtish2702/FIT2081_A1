package com.fit2081.arrtish.id32896786.a1.databases

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    // Convert Date to Long (timestamp)
    @TypeConverter
    fun fromDateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // Convert Long (timestamp) back to Date
    @TypeConverter
    fun fromTimestampToDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}
