package dk.itu.moapd.x9.alyp

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.database.database

private const val DATABASE_URL = "https://moapd-2026-bf43d-default-rtdb.europe-west1.firebasedatabase.app/reports{uid}"

/**
 * Application.onCreate() called by system when application is first loaded into memory i.e. 'launches' and destroyed when X9 process is destroyed.
 * Not re-created upon configuration changes.
 * Good place to do any kind of one-time initialization operations
 * Offline perisstence enabled
 */
class X9Application : Application() {
    override fun onCreate() {
        super.onCreate()
//        Firebase.database(DATABASE_URL).setPersistenceEnabled(true)
        ReportRepository.initialize()
    }
}