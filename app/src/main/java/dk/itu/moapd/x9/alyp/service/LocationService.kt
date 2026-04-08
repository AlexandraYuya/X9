package dk.itu.moapd.x9.alyp.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.telecom.TelecomManager.EXTRA_LOCATION
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class LocationService : Service() {

    /**
     * A set of private constants used in this class.
     */
    companion object {
        /**
         * The interval for active location updates. Updates may be less frequent than this interval
         * if the app is not in the foreground.
         */
        private const val LOCATION_UPDATE_INTERVAL_MS = 1000L

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private const val MIN_UPDATE_INTERVAL_MS = 1000L

        /**
         * The maximum time when batched location updates are delivered. Updates may be
         * delivered sooner than this interval.
         */
        private const val MAX_UPDATE_DELAY_MS = 100L
    }

    inner class LocalBinder : Binder() {
        val service: LocationService
            get() = this@LocationService
    }
    private val localBinder = LocalBinder()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val _locationUpdates = MutableSharedFlow<Location>(replay = 1)
    val locationUpdates = _locationUpdates.asSharedFlow()

    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                locationResult.lastLocation?.let {
                    _locationUpdates.tryEmit(it)
                }
                val currentLocation = locationResult.lastLocation
                val intent = Intent()
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startForeground("location_tracking_channel", createNotification())
//        return START_NOT_STICKY
//    }
//
    override fun onBind(intent: Intent): IBinder = localBinder

    /**
     * Subscribes this application to get the location changes via the `locationCallback()`.
     */
    fun subscribeToLocationUpdates() {
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL_MS)
            .setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL_MS)
            .setMaxUpdateDelayMillis(MAX_UPDATE_DELAY_MS)
            .build()

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper(),
            )
        } catch (unlikely: SecurityException) {
//            LocationTrackingPreferences.setTrackingEnabled(this, false)
        }
    }

    /**
     * Unsubscribes this application from location changes.
     */
    fun unsubscribeToLocationUpdates() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        } catch (_: SecurityException) {
//            LocationTrackingPreferences.setTrackingEnabled(this, true)
        }
    }
}