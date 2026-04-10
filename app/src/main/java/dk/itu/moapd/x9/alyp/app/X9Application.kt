package dk.itu.moapd.x9.alyp.app

import android.app.Application
import android.util.Log
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.material.color.DynamicColors
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dk.itu.moapd.x9.alyp.core.DATABASE_URL
private const val TAG = "X9Application"
/**
 * Application.onCreate() called by system when application is first loaded into memory i.e. 'launches' and destroyed when X9 process is destroyed.
 * Not re-created upon configuration changes.
 * Good place to do any kind of one-time initialization operations
 * Offline perisstence enabled
 */
class X9Application : Application(), OnMapsSdkInitializedCallback {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Firebase.database(DATABASE_URL).setPersistenceEnabled(true)
        Firebase.database(DATABASE_URL).reference.keepSynced(true)
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, this)
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> {
                Log.d(TAG, "The latest version of the renderer is used.")
            }
            MapsInitializer.Renderer.LEGACY -> {
                Log.d(TAG, "The legacy version of the renderer is used.")
            }
        }
    }
}