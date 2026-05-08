package dk.itu.moapd.x9.alyp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dk.itu.moapd.x9.alyp.core.DATABASE_URL

/**
 * Handles Geofence transitions, receives the intent from pendingIntent from the request to add geofences.
 * Inspired by Android developers official document on 'Geofencing API': https://developer.android.com/develop/sensors-and-location/location/geofencing
 */
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "GeofenceBroadcastReceiver"
        const val EXTRA_NEW_REPORT_UID = "newReportUid"
        const val GEOFENCE_RADIUS_METERS = 200f
        const val PATH_CONFIRMATIONS = "confirmations"
    }
    private val databaseRef = Firebase.database(DATABASE_URL).reference

    override fun onReceive(context: Context, intent: Intent) {
        // Retrieves information on which report event was triggered via the intent.
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Geofencing error code: ${geofencingEvent.errorCode}")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // get the geofences which were triggered
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            // get the report which triggered the geofence
            val newReportUid = intent.getStringExtra(EXTRA_NEW_REPORT_UID) ?: return

            // for each triggered geofence, add it to database's confirmation entry
            triggeringGeofences?.forEach { geofence ->
                databaseRef
                    .child(PATH_CONFIRMATIONS)
                    .child(geofence.requestId)
                    .setValue(true)
            }
            // the triggering report also gets added to database confirmation entry
            databaseRef
                .child(PATH_CONFIRMATIONS)
                .child(newReportUid)
                .setValue(true)

        }else {
            Log.e(TAG, "invalid geofencing type $geofenceTransition")
        }
    }
}