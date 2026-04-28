package dk.itu.moapd.x9.alyp.repository

import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import dk.itu.moapd.x9.alyp.core.DATABASE_URL
import dk.itu.moapd.x9.alyp.model.Report
import kotlinx.coroutines.tasks.await

/**
 * Repository for reading and writing report data to Firebase Realtime Database.
 *
 * Encapsulates all database access logic so that reportViewModel does not interact with Firebase directly.
 * All report data is stored under two root paths "reports" and "confirmations":
 *  - "/reports/{userId}/{reportUid}" report objects owned by each user
 *  - "/confirmations/{reportUid}/ — tracks which reports have been confirmed
 */
class ReportRepository(private val database: DatabaseReference = Firebase.database(DATABASE_URL).reference){
    companion object {
        private const val PATH_REPORTS = "/reports"
        private const val CHILD_CREATED_AT = "createdAt"
        private const val PATH_CONFIRMATIONS = "confirmations"
    }

    private suspend fun isReportConfirmed(reportUid: String): Boolean {
        return database
            .child(PATH_CONFIRMATIONS)
            .child(reportUid)
            .get()
            .await()
            .getValue(Boolean::class.java) ?: false
    }

    /**
     * Fetches all reports belonging to a specific user, ordered by 'createdAt'.
     *
     * @param userId The firebase uid of the user whose reports to fetch.
     * @return A list of report objects, or empty if userId is null.
     */
    suspend fun getUserReports(userId: String?): List<Report> {
        userId ?: return emptyList()
        return database
            .child(PATH_REPORTS)
            .child(userId)
            .orderByChild(CHILD_CREATED_AT)
            .get()
            .await()
            .children
            .mapNotNull { snapshot ->
                snapshot.getValue(Report::class.java)
            }
    }

    /**
     * Fetches all reports from all users, ordered by 'createdAt'.
     * returns reports with a isConfirmed check
     *
     * @return A list of all report objects from all users.
     */
    suspend fun getPublicReports(): List<Report> {
        val reports =  database
            .child(PATH_REPORTS)
            .orderByChild(CHILD_CREATED_AT)
            .get()
            .await()
            .children
            .flatMap { snapshot ->
                snapshot.children.mapNotNull {
                    it.getValue(Report::class.java)
                }
            }
        return reports.map { report ->
            report.copy(isConfirmed = isReportConfirmed(report.uid))
        }
    }

    /**
     * Saves a new report to the database under the given user's reports path.
     *
     * @param userId The Firebase uid of the user whose reports to add to.
     * @param report The report to save. Uses uid as the database key.
     */
    suspend fun addUserReport(userId: String?, report: Report) {
        userId ?: return
        database
            .child(PATH_REPORTS)
            .child(userId)
            .child(report.uid)
            .setValue(report)
            .await()
    }

    /**
     * Deletes a specific report from the database.
     *
     * @param userId The Firebase uid of the user whose report clear.
     * @param reportUid The uid of the report to delete.
     */
    suspend fun deleteUserReport(userId: String?, reportUid: String) {
        userId ?: return
        database
            .child(PATH_REPORTS)
            .child(userId)
            .child(reportUid)
            .removeValue()
            .await()
        database
            .child(PATH_CONFIRMATIONS)
            .child(reportUid)
            .removeValue()
            .await()
    }
}