package dk.itu.moapd.x9.alyp

import android.content.Context
import androidx.room.Room
import dk.itu.moapd.x9.alyp.database.ReportDatabase
import java.util.UUID

/**
 * Repo encapsulates the logic for how to fetch and store a particular set of data,
 * whether locally in a database or from a remote server.
 *
 * ReportRepository is a singleton, meaning there will only ever be one instance of it in X9
 */

private const val DATABASE_NAME = "report-database"
class ReportRepository private constructor(context: Context){

    private val database: ReportDatabase = Room.databaseBuilder(
        context.applicationContext,
        ReportDatabase::class.java,
        DATABASE_NAME
    ).build()

    suspend fun getReports(): List<Report> = database.reportDao().getReports()
    suspend fun getReport(id: UUID): Report = database.reportDao().getReport(id)
    suspend fun addReport(report: Report) = database.reportDao().addReport(report)
    suspend fun clearReports() = database.reportDao().clearReports()

    companion object {
        private var INSTANCE: ReportRepository? = null // marked private so no external component can create their own instance

        // initialize a new instance of the repo
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = ReportRepository(context)
            }
        }

        // access the initialized repo
        fun get(): ReportRepository {
            return INSTANCE ?:
            throw IllegalStateException("ReportRepository must be initialized")
        }
    }
}