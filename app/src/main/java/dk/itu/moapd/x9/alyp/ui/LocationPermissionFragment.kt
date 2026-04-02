package dk.itu.moapd.x9.alyp.ui

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.core.Context
import dk.itu.moapd.x9.alyp.service.LocationService
import dk.itu.moapd.x9.alyp.R
import dk.itu.moapd.x9.alyp.databinding.FragmentLocationPermissionBinding

class LocationPermissionFragment : DialogFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var _binding: FragmentLocationPermissionBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
//    private val sharedPreferences: SharedPreferences by lazy {
//        requireActivity().getSharedPreferences(
//            getString(dk.itu.moapd.x9.alyp.PREFERENCE_FILE_KEY),
//            Context.MODE_PRIVATE,
//        )
//    }

    /**
     * Provides location updates for while-in-use feature.
     */
    private var locationService: LocationService? = null

    /**
     * A flag to indicate whether a bound to the service.
     */
    private var locationServiceBound: Boolean = false

    /**
     * When a start-tracking request happens but the service is not yet bound, this flag marks a
     * pending request. Once the service bind completes we will subscribe to updates.
     */
    private var pendingStartTracking: Boolean = false

//    private val serviceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName, service: IBinder) {
//            val binder = service as LocationService.LocalBinder
//            locationService = binder.service
//            locationServiceBound = true
//
//            if (pendingStartTracking) {
//                locationService?.subscribeToLocationUpdates()
//                pendingStartTracking = false
//            }
//
//            locationService?.let { svc ->
//                viewLifecycleOwner.lifecycleScope.launch {
//                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                        svc.locationUpdates.collect(::updateLocationDetails)
//                    }
//                }
//            }
//        }
//
//        override fun onServiceDisconnected(name: ComponentName) {
//            locationService = null
//            locationServiceBound = false
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonState.setOnClickListener {
//            requestLocationPermission()
//            if (LocationTrackingPreferences.isTrackingEnabled(requireContext())) {
//                resetLocationDetails()
//                locationService?.unsubscribeToLocationUpdates()
//                pendingStartTracking = false
//                requireActivity().stopService(
//                    Intent(
//                        requireContext(), LocationService::class.java
//                    )
//                )
//            } else {
//                if (hasLocationPermission()) {
//                    startLocationTracking()
//                } else {
//                    requestLocationPermission()
//                }
//            }
        }
    }

    override fun onStart() {
        super.onStart()

//        updateButtonState(
//            LocationTrackingPreferences.isTrackingEnabled(requireContext())
//        )
//        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

//        val serviceIntent = Intent(requireContext(), LocationService::class.java)
//        requireActivity().bindService(
//            serviceIntent,
//            serviceConnection,
//            Context.BIND_AUTO_CREATE
//        )
//
//        val alreadyEnabled = LocationTrackingPreferences.isTrackingEnabled(requireContext())
//        if (alreadyEnabled) {
//            startLocationTracking()
//        }
    }

    override fun onStop() {
//        if (locationServiceBound) {
//            requireActivity().unbindService(serviceConnection)
//            locationServiceBound = false
//        }
//
//        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
//        if (key == LocationTrackingPreferences.KEY_TRACKING_ENABLED) {
//            updateButtonState(
//                LocationTrackingPreferences.isTrackingEnabled(requireContext())
//            )
//        }
    }

//    private fun startLocationTracking() {
//        pendingStartTracking = true
//        val serviceIntent = Intent(requireContext(), LocationService::class.java)
//        ContextCompat.startForegroundService(requireActivity(), serviceIntent)
//        if (locationServiceBound) {
//            locationService?.subscribeToLocationUpdates()
//            pendingStartTracking = false
//        }
//    }
//
//    private fun updateButtonState(trackingLocation: Boolean) {
//        binding.buttonState.text = getString(
//            if (trackingLocation) R.string.button_stop else R.string.button_start,
//        )
//    }

//    private fun updateLocationDetails(location: Location) {
//        with(binding) {
//            editTextLatitude.setText(
//                String.format(Locale.getDefault(), "%.6f", location.latitude)
//            )
//            editTextLongitude.setText(
//                String.format(Locale.getDefault(), "%.6f", location.longitude)
//            )
//            editTextAltitude.setText(
//                String.format(Locale.getDefault(), "%.6f", location.altitude)
//            )
//            editTextSpeed.setText(
//                getString(R.string.text_speed_km, (location.speed * 3.6f).toInt())
//            )
//            editTextTime.setText(
//                location.time.toSimpleDateTimeString()
//            )
//        }
//    }

//    private fun resetLocationDetails() {
//        with(binding) {
//            editTextLatitude.setText(getString(R.string.text_not_available))
//            editTextLongitude.setText(getString(R.string.text_not_available))
//            editTextAltitude.setText(getString(R.string.text_not_available))
//            editTextSpeed.setText(getString(R.string.text_not_available))
//            editTextTime.setText(getString(R.string.text_not_available))
//        }
//    }
}