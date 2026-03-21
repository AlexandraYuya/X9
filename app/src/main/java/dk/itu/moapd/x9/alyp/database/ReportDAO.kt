package dk.itu.moapd.x9.alyp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dk.itu.moapd.x9.alyp.Report
import java.util.UUID

/**
 * Data access object, an interface that contains functions for each DB operation. When connection DAO to DB class, Room will generate implementations of the functions you add to this
 * interface.
 * Two query functions, both meant to pull information out of the DB:
 *  - One to return a list of all reports in the DB
 *  - One to return a single report matching a iven UUID
 */
@Dao
interface ReportDAO {
    @Query("SELECT * FROM report")
    suspend fun getReports(): List<Report> // implement as suspending functions, runs within a coroutine

    @Query("SELECT * FROM report WHERE id=(:id)")
    suspend fun getReport(id: UUID): Report

    @Query("DELETE FROM report")
    suspend fun clearReports()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReport(report: Report)
}