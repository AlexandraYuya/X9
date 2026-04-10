package dk.itu.moapd.x9.alyp.ui

import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import dk.itu.moapd.x9.alyp.core.DATABASE_URL
import dk.itu.moapd.x9.alyp.model.Report
import kotlinx.coroutines.tasks.await

/**
 * Repo encapsulates the logic for how to fetch and store a particular set of data,
 * whether locally in a database or from a remote server.
 *
 * ReportRepository is a singleton, meaning there will only ever be one instance of it in X9
 */
private const val TAG = "ReportRepository"

class ReportRepository(
    private val database: DatabaseReference = Firebase.database(DATABASE_URL).reference
){
    companion object {
        /**
         * The path to the "reports" node in the database.
         */
        private const val PATH_REPORTS = "/reports"

        /**
         * The child key for the "createdAt" field in the database.
         */
        private const val CHILD_CREATED_AT = "createdAt"
    }

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

    suspend fun getPublicReports(): List<Report> {
        return database
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
    }

    fun addUserReport(userId: String?, report: Report) {
        userId ?: return
        val key = database
            .child(PATH_REPORTS)
            .child(userId)
            .push()
            .key ?: return
        database
            .child(PATH_REPORTS)
            .child(userId)
            .child(key)
            .setValue(report)
    }
//    fun clearUserReports(userId: String, key: String) {
//        database
//            .child(PATH_REPORTS)
//            .child(userId)
//            .child(key)
//            .removeValue()
//    }
}