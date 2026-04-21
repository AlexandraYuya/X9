package dk.itu.moapd.x9.alyp.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dk.itu.moapd.x9.alyp.ui.CameraFragment.Companion.TAG
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * LocationService provides continuous GPS location updates to clients.
 * Uses FusedLocationProviderClient to request hugh accuracy location updates and emits them via a SharedFlow which clients can collect from.
 *
 * Location updates are requested every 2 minutes.
 * Updates are never more frequent than every 1 minute.
 * Batched updates are delivered at most every 10 minutes.
 */
class LocationService : Service() {

    /**
     * A set of private constants.
     */
    companion object {
        private const val TAG = "LocationService"

        /**
         * The interval for active location updates. Updates may be less frequent than this interval
         * if the app is not in the foreground.
         */
        private const val LOCATION_UPDATE_INTERVAL_MS = 2 * 60 * 1000L

        /**
         * The fastest rate for active location updates. Updates will never be more frequent than this value.
         */
        private const val MIN_UPDATE_INTERVAL_MS = 1 * 60 * 1000L

        /**
         * The maximum time when batched location updates are delivered. Updates may be
         * delivered sooner than this interval.
         */
        private const val MAX_UPDATE_DELAY_MS = 10 * 60 * 1000L
    }

    /**
     * Allows clients to get a direct reference to the locationService instance.
     */
    inner class LocalBinder : Binder() {
        val service: LocationService
            get() = this@LocationService
    }
    private val localBinder = LocalBinder()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val _locationUpdates = MutableSharedFlow<Location>(replay = 1) // holds the last known location for new collectors
    val locationUpdates = _locationUpdates.asSharedFlow()

    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this) // client for recieving location permissions, entry point to use location APIs
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                locationResult.lastLocation?.let { // check if there's a location, then retrieve it
                    _locationUpdates.tryEmit(it)// updates
                }
            }
        }
    }

    /**
     * Called when a client binds to this service.
     * Returns a localBinder giving the client direct access to this service instance.
     */
    override fun onBind(intent: Intent): IBinder = localBinder

    /**
     * Subscribes to location updates via the locationCallback().
     * Builds a locationRequest with high accuracy.
     */
    fun subscribeToLocationUpdates() {
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL_MS) // Accuracy: specify location accuracy. Frequency: the interval of computing app's location.
            .setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL_MS) // Frequency: specify the interval for recieving other apps' locations
            .setMaxUpdateDelayMillis(MAX_UPDATE_DELAY_MS) // Latency: specify latency. Delays location delivery, multiple location updates may be delivered in batches. i.e. specifies the interval at which location is delivered to the app. Should be multiple times larger than location computing frequency 'setIntervalMillis'
            .build()

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper(),
            )
        } catch (ex: SecurityException) {
            Log.e(TAG, "Location permission not granted: ${ex.message}", ex)
        }
    }

    /**
     * Unsubscribes from location updates, stopping all further location delivery.
     */
    fun unsubscribeToLocationUpdates() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        } catch (ex: SecurityException) {
            Log.e(TAG, "Failed to remove location updates: ${ex.message}", ex)
        }
    }
}