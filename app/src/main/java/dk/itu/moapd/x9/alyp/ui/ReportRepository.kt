package dk.itu.moapd.x9.alyp.ui

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import dk.itu.moapd.x9.alyp.model.Report
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Repo encapsulates the logic for how to fetch and store a particular set of data,
 * whether locally in a database or from a remote server.
 *
 * ReportRepository is a singleton, meaning there will only ever be one instance of it in X9
 */
private const val TAG = "ReportRepository"
private const val DATABASE_URL = "https://moapd-2026-bf43d-default-rtdb.europe-west1.firebasedatabase.app/"

class ReportRepository private constructor(){
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database(DATABASE_URL)
    private fun reportsRef() = auth.currentUser?.uid?.let { uid ->
        database.reference.child("reports").child(uid)
    }
    fun getReports(): Flow<List<Report>> = callbackFlow {
        val ref = reportsRef()

        if(ref == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val query = ref.orderByChild("createdAt")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reports = snapshot.children.mapNotNull { child ->
                    child.getValue(Report::class.java)
                }.sortedBy { it.createdAt }
                trySend(reports)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database read cancelled", error.toException())
                trySend(emptyList())
                close()
            }
        }
        query.addValueEventListener(listener)

        awaitClose {
            query.removeEventListener(listener)
        }
    }
    fun addReport(report: Report) {
        val ref = reportsRef() ?: throw IllegalStateException("User must be logged in")
        ref.child(report.uid).setValue(report)
    }
    fun clearReports() {
        val ref = reportsRef() ?: throw IllegalStateException("User must be logged in")
        ref.removeValue()
    }

    companion object {
        private var INSTANCE: ReportRepository? = null // marked private so no external component can create their own instance

        // initialize a new instance of the repo
        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = ReportRepository()
            }
        }

        // access the initialized repo
        fun get(): ReportRepository {
            return INSTANCE ?: throw IllegalStateException("ReportRepository must be initialized")
        }
    }
}