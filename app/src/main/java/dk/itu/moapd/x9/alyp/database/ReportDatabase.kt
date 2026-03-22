package dk.itu.moapd.x9.alyp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dk.itu.moapd.x9.alyp.Report

/**
 * @Database annotation tells Room that this class represents a DB in X9 app.
 * First param is a list of entity classes, tells Room which entity classes to use when creating and managing tables for this DB.
 * Second param is the version if the DB. When modyfying or adding entites, increment version to notify Room somethinf has changed.
 * @TypeConverters annotation tells DB to use functions defined in the passed class.
 *
 * When the DB is created, Room will generate a concrete and accesible implementation of the DAO.
 */
@Database(entities = [Report::class], version = 3)
@TypeConverters(ReportTypeConverters::class)
abstract class ReportDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDAO
}