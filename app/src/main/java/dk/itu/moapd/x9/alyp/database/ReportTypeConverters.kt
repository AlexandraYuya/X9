package dk.itu.moapd.x9.alyp.database

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class ReportTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }

    @TypeConverter
    fun fromUuid(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUuid(uuid: String): UUID {
        return UUID.fromString(uuid)
    }
}