package dk.itu.moapd.x9.alyp.repository

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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Repository for reading and writing report data to Firebase Realtime Database.
 *
 * Encapsulates all database access logic so that reportViewModel does not interact with Firebase directly.
 * All report data is stored under two root paths "reports" and "upvotes":
 *  "/reports/{userId}/{reportUid}" report objects owned by each user
 *  "/upvotes/{reportUid}/count" — upvote count per report
 *  "/upvotes/{reportUid}/voters/{userId}" — tracks which users have voted st. user's cannot vote again on a report
 */
class ReportRepository(private val database: DatabaseReference = Firebase.database(DATABASE_URL).reference){
    companion object {
        private const val PATH_REPORTS = "/reports"
        private const val PATH_UPVOTES = "/upvotes"
        private const val CHILD_CREATED_AT = "createdAt"
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
     * Each report's upvoteCount is populated from the /upvotes/ path.
     *
     * @return A list of all report objects from all users.
     */
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
    }

    /**
     * Fetches the current upvote count for a report.
     *
     * @param reportUid The uid to fetch the count for.
     * @return The current upvote count or 0 if none exist.
     */
    suspend fun getUpvoteCount(reportUid: String): Int {
        return database
            .child(PATH_UPVOTES)
            .child(reportUid)
            .child("count")
            .get().await().getValue(Int::class.java) ?: 0
    }

    /**
     * Checks whether a user has already upvoted a specific report.
     *
     * @param reportUid The report uid to check.
     * @param userId The Firebase uid of the user to check.
     * @return True if the user has already voted, false otherwise.
     */
    suspend fun hasUserVoted(reportUid: String, userId: String): Boolean {
        return database
            .child(PATH_UPVOTES)
            .child(reportUid)
            .child("voters")
            .child(userId)
            .get().await().exists()
    }

    /**
     * Increments the upvote count for a report using a Firebase transaction to prevent race conditions, and records the voter to prevent duplicate votes.
     *
     * @param reportUid The report uid to upvote.
     * @param userId The Firebase uid of the voting user.
     * @return A pair where pair.first is true if the vote was committed successfully and pair.second is the updated upvote count.
     */
    suspend fun upvoteReport(reportUid: String, userId: String): Pair<Boolean, Int> =
        suspendCoroutine { continuation ->
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
                        continuation.resume(Pair(true, snapshot?.getValue(Int::class.java) ?: 0))
                    } else {
                        continuation.resume(Pair(false, 0))
                    }
                }
            })
        }
}