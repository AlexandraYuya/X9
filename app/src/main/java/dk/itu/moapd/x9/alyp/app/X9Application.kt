package dk.itu.moapd.x9.alyp.app

import android.app.Application
import android.util.Log
import com.google.android.gms.maps.MapsInitializer
import com.google.android.material.color.DynamicColors
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dk.itu.moapd.x9.alyp.core.DATABASE_URL
private const val TAG = "X9Application"
/**
 * Application.onCreate() called by system when application is first loaded into memory i.e. 'launches' and destroyed when X9 process is destroyed.
 * Not re-created upon configuration changes.
 * Good place to do any kind of one-time initialization operations
 * Offline perisstence enabled, cache data locally.
 */
class X9Application : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() called")
        // Uses dynamic colours defined in the themes.xml & colors.xml files.
        DynamicColors.applyToActivitiesIfAvailable(this)
        Firebase.database(DATABASE_URL).setPersistenceEnabled(true)
        Firebase.database(DATABASE_URL).reference.keepSynced(true)
    }
}