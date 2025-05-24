package com.fit2081.arrtish.id32896786.a1.databases
/**
 * Disclaimer:
 * This file may include comments or documentation assisted by OpenAI's GPT model.
 * All code logic and architectural decisions were implemented and verified by the developer.
 */
import androidx.room.TypeConverter
import java.util.*

/**
 * TypeConverter class to handle conversion between Date objects and Long timestamps
 * for storing Date fields in the Room database.
 *
 * Room does not support Date natively, so these converters are required.
 */
class DateConverter {

    /**
     * Converts a Date object to a Long timestamp for database storage.
     * @param date The Date to convert, nullable.
     * @return The timestamp in milliseconds since epoch, or null if date is null.
     */
    @TypeConverter
    fun fromDateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    /**
     * Converts a Long timestamp back to a Date object when reading from the database.
     * @param timestamp The Long timestamp in milliseconds since epoch, nullable.
     * @return The corresponding Date object or null if timestamp is null.
     */
    @TypeConverter
    fun fromTimestampToDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}
