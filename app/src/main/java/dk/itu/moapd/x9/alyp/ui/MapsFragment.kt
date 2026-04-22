package dk.itu.moapd.x9.alyp.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapColorScheme
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.x9.alyp.databinding.FragmentMapsBinding
import dk.itu.moapd.x9.alyp.model.Report
import dk.itu.moapd.x9.alyp.service.LocationService
import dk.itu.moapd.x9.alyp.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class MapsFragment : Fragment() {
    companion object {
        private const val TAG = "MapsFragment"
    }

    /**
     * Fields + properties
     */
    private var _binding: FragmentMapsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val reportViewModel: ReportViewModel by activityViewModels()
    private var googleMap: GoogleMap? = null

    /**
     * Provides location updates for while-in-use feature.
     */
    private var locationService: LocationService? = null

    /**
     * A flag to indicate whether the fragment is bound to the service.
     */
    private var locationServiceBound: Boolean = false

    /**
     * Permission launcher. Called when callback is initiated, and a location permission isn't found.
     * checks if location permission has been granted, if so subscribe to location service.
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            enableMyLocation()
            locationService?.subscribeToLocationUpdates()
        } else {
            // Use view (nullable) to avoid crashes if view is destroyed
            view?.let {
                Snackbar.make(
                    it,
                    "Location permission, not enabled. To be able to track current position. Please allow location permission.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Establish service connection
     * Permission already granted, onStart() calls serviceConnection
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true

            if (checkPermission()) { // checks if user already gave permission, if not then skips location subscription
                locationService?.subscribeToLocationUpdates()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
            locationServiceBound = false
        }
    }

    /**
     * Map callback, called when map is ready to be used
     * A callback interface that handles events and user interaction for the GoogleMap object.
     */
    private val callback = OnMapReadyCallback { googleMap ->

        // Update the Google Maps object. The entry point for managing the underlying map features and data.
        this.googleMap = googleMap

        // We use the view's root to find out how big the system bars are.
        view?.let { fragmentView ->
            ViewCompat.setOnApplyWindowInsetsListener(fragmentView) { _, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                // It automatically pushes UI buttons below the status bar and above the navigation
                // bar.
                googleMap.setPadding(0, systemBars.top, 0, systemBars.bottom)

                insets
            }
            ViewCompat.requestApplyInsets(fragmentView)
        }

        // Add a marker in IT University of Copenhagen and move the camera.
        val itu = LatLng(55.6596, 12.5910)
        googleMap.addMarker(MarkerOptions()
            .position(itu)
            .title("IT University of Copenhagen"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(itu))
        googleMap.isTrafficEnabled = true

        // Set the Google Maps style.
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.mapColorScheme = MapColorScheme.FOLLOW_SYSTEM

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reportViewModel.reports.collect { reports ->
                    googleMap.clear()
                    reports.forEach { report ->
                        if (report.latitude != 0.0 && report.longitude != 0.0) {
                            addReportMarker(report, googleMap)
                        }
                    }
                }
            }
        }

        // Enable the location layer. Request the permission if it is not granted. Callback will catch if user hasn't granted permission yet and will call requestUserPermissions()
        if (checkPermission()) {
            @Suppress("MissingPermission")
            googleMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Lifecycle methods
     */
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment? // SupportMapFragment: A fragment for managing the lifecycle of a GoogleMap object.
        mapFragment?.getMapAsync(callback)
    }

    override fun onStart() {
        super.onStart()

        val serviceIntent = Intent(requireContext(), LocationService::class.java)
        requireActivity().bindService( // create connection to LocationService
            serviceIntent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()
        if (locationServiceBound) {
            locationService?.unsubscribeToLocationUpdates()
            requireActivity().unbindService(serviceConnection)
            locationServiceBound = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Private helper methods
     */

    private fun addReportMarker(report: Report, googleMap: GoogleMap) {
        val hue = when  {
            report.upvoteCount >= 10 -> BitmapDescriptorFactory.HUE_RED
            report.upvoteCount >= 3 -> BitmapDescriptorFactory.HUE_ORANGE
            else -> BitmapDescriptorFactory.HUE_AZURE
        }
        googleMap.addMarker(MarkerOptions()
            .position(LatLng(report.latitude, report.longitude))
            .title(report.title)
            .icon(BitmapDescriptorFactory.defaultMarker(hue))
        )
    }
    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun enableMyLocation() {
        try {
            if (checkPermission()) {
                googleMap?.isMyLocationEnabled = true
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Cannot enable location: ${e.message}")
        }
    }
}