package dk.itu.moapd.x9.alyp.app

import android.app.Application
import android.util.Log
import com.google.android.material.color.DynamicColors
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dk.itu.moapd.x9.alyp.core.DATABASE_URL
private const val TAG = "X9Application"
/**
 * Application.onCreate() called by system when application is first loaded into memory i.e. 'launches' and destroyed when X9 process is destroyed.
 * Not re-created upon configuration changes.
 * Good place to do any kind of one-time initialization operations
 * Offline persistence enabled, cache data locally.
 */
class X9Application : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() called")
        // Uses dynamic colours defined in the themes.xml & colors.xml files.
        DynamicColors.applyToActivitiesIfAvailable(this)
        val db = Firebase.database(DATABASE_URL)
        db.setPersistenceEnabled(true)
        db.reference.keepSynced(true)
    }
}
