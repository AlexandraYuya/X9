package dk.itu.moapd.x9.alyp.ui

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
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
        private const val PATH_UPVOTES = "/upvotes"

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
            .map { report ->
                report.copy(upvoteCount = getUpvoteCount(report.uid))
            }
    }

    fun addUserReport(userId: String?, report: Report) {
        userId ?: return
        database
            .child(PATH_REPORTS)
            .child(userId)
            .child(report.uid)
            .setValue(report)
    }
//    fun clearUserReports(userId: String, key: String) {
//        database
//            .child(PATH_REPORTS)
//            .child(userId)
//            .child(key)
//            .removeValue()
//    }

    suspend fun getUpvoteCount(reportUid: String): Int {
        return database
            .child(PATH_UPVOTES)
            .child(reportUid)
            .child("count")
            .get().await().getValue(Int::class.java) ?: 0
    }

    suspend fun hasUserVoted(reportUid: String, userId: String): Boolean {
        return database
            .child(PATH_UPVOTES)
            .child(reportUid)
            .child("voters")
            .child(userId)
            .get().await().exists()
    }

    fun upvoteReport(reportUid: String, userId: String, onComplete: (success: Boolean, newCount: Int) -> Unit) {
        val countRef = database.child(PATH_UPVOTES).child(reportUid).child("count")
        val voterRef = database.child(PATH_UPVOTES).child(reportUid).child("voters").child(userId)

        countRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                currentData.value = (currentData.getValue(Int::class.java) ?: 0) + 1
                return Transaction.success(currentData)
            }
            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (committed && error == null) {
                    voterRef.setValue(true)
                    onComplete(true, snapshot?.getValue(Int::class.java) ?: 0)
                } else {
                    onComplete(false, 0)
                }
            }
        })
    }
}