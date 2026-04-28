package dk.itu.moapd.x9.alyp.ui

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.alyp.R
import dk.itu.moapd.x9.alyp.databinding.FragmentReportFormBinding
import dk.itu.moapd.x9.alyp.model.Report
import dk.itu.moapd.x9.alyp.service.GeofenceBroadcastReceiver
import dk.itu.moapd.x9.alyp.viewmodel.CameraViewModel
import dk.itu.moapd.x9.alyp.viewmodel.ReportViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.getValue

/**
 * ReportFormFragment allows a signed-in user to submit a new incident report.
 *
 * Provides input fields for title, location, date/time, type, description and severity.
 * Geocodes the entered address to latitude/longitude coordinates upon submission using Android's built-in Geocoder.
 * Optionally attaches a photo captured via CameraFragment.
 *
 * Navigates to CameraFragment when the camera button is clicked, and back to ReportListFragment on successful report submission.
 */
class ReportFormFragment : Fragment() {
    companion object {
        private const val TAG = "ReportFormFragment"
        const val GEOFENCE_EXPIRATION_IN_MILLISECONDS = 60_000L
    }
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentReportFormBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val reportViewModel: ReportViewModel by activityViewModels()
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "onCreateView() called")
        _binding = FragmentReportFormBinding.inflate(inflater, container, false).also {
            auth = FirebaseAuth.getInstance()
        }
        return binding.root
    }

    /**
     * Sets up the report type dropdown, date field, camera button listener, image thumbnail observer, and submit button listener.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        // dropdown menu for report types
        val types = resources.getStringArray(R.array.report_types_array)
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            types
        ).also { adapter ->
            binding.reportTypeInput.setAdapter(adapter)
        }

        // format Date
        binding.reportDateTimeInput.setText(dateFormatter.format(Date()))

        // camera click event listener to launch camera app
        binding.reportCamera.setOnClickListener {
            // launch camera fragment if has permission
            findNavController().navigate(R.id.action_reportFormFragment_to_reportCameraFragment)
        }

        cameraViewModel.localImageUri.observe(viewLifecycleOwner) { uri ->
            if (uri !== null) {
                binding.reportPhoto.setImageURI(uri)
            }
        }


        binding.submitBtn.setOnClickListener {
            if (auth.currentUser != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    submitReport()
                }
            }else {
                Toast.makeText(context, "Sign-in to submit a report", Toast.LENGTH_SHORT).show()
            }
        }

        // Autofill location from GPS
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(requireActivity())
                .lastLocation
                .addOnSuccessListener { location ->
                    if (location == null) return@addOnSuccessListener
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        val address = Geocoder(requireContext(), Locale.getDefault())
                            .getFromLocation(location.latitude, location.longitude, 1)
                            ?.firstOrNull()?.getAddressLine(0) ?: return@launch
                        withContext(Dispatchers.Main) {
                            binding.reportLocationInput.setText(address)
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**
     * Validates input, geocodes the entered location address to latLng coordinates, builds a Report object and submits it via ReportViewModel.
     * Shows a toast txt and navigates to ReportListFragment on success.
     */
    private suspend fun submitReport() {
        if(!validateInput()) return

        val checkedId = binding.buttonToggleGroup.checkedButtonId

        if (checkedId == View.NO_ID) {
            Toast.makeText(requireContext(), "Please select a severity level", Toast.LENGTH_SHORT).show()
            return
        }

        val severity = when (checkedId) {
            R.id.button_minor -> getString(R.string.severity_minor)
            R.id.button_moderate -> getString(R.string.severity_moderate)
            R.id.button_major -> getString(R.string.severity_major)
            else -> getString(R.string.error_required)
        }

        // parse date&time at submit time
        val dateTimeText = binding.reportDateTimeInput.text.toString().trim()
        val reportDate = dateFormatter.parse(dateTimeText)?.time ?: System.currentTimeMillis()

        val coordinates = withContext(Dispatchers.IO) {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            geocoder.getFromLocationName(binding.reportLocationInput.text.toString().trim(), 1)?.firstOrNull()
        }

        if (coordinates == null) {
            Toast.makeText(requireContext(), "Could not find location, try a more specific address", Toast.LENGTH_SHORT).show()
            return
        }

        val report = Report(
            uid = UUID.randomUUID().toString(),
            title = binding.reportTitleInput.text.toString().trim(),
            location = binding.reportLocationInput.text.toString().trim(),
            latitude = coordinates.latitude,
            longitude = coordinates.longitude,
            createdAt = reportDate,
            type = binding.reportTypeInput.text.toString().trim(),
            description = binding.reportDescriptionInput.text?.toString()?.trim() ?: "",
            severity = severity,
            user = auth.currentUser?.email.toString(),
            imageUrl = cameraViewModel.imageUri.value?.toString() ?: "",
        )

        reportViewModel.addUserReport(report)
        registerConfirmationGeofences(report)

        Toast.makeText(requireContext(), "Report stored successfully!", Toast.LENGTH_SHORT).show()

        findNavController().navigate(R.id.action_reportFormFragment_to_reportListFragment)
    }

    /**
     * Validator to check if form text fields are blank or wrongly formatted
     */
    private fun validateInput(): Boolean {
        with(binding) {
            reportTitleLayout.error = null
            reportLocationLayout.error = null
            reportDateLayout.error = null
            reportTypeLayout.error = null

            var ok = true
            val reportDate = reportDateTimeInput.text?.toString()?.trim().orEmpty()

            if (reportTitleInput.text?.toString()?.trim().orEmpty().isBlank()) {
                binding.reportTitleLayout.error = getString(R.string.error_required)
                ok = false
            }
            if (reportLocationInput.text?.toString()?.trim().orEmpty().isBlank()) {
                binding.reportLocationLayout.error = getString(R.string.error_required)
                ok = false
            }
            if (reportDate.isBlank()) {
                binding.reportDateLayout.error = getString(R.string.error_required)
                ok = false
            } else if(!reportDate.matches(Regex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$"))) {
                binding.reportDateLayout.error = getString(R.string.error_date_format)
                ok = false
            }
            if (reportTypeInput.text?.toString()?.trim().orEmpty().isBlank()) {
                binding.reportTypeLayout.error = getString(R.string.error_required)
                ok = false
            }

            return ok
        }
    }

    /**
     * Registers geofences around all existing reports of the same type as newReport using GeofencingClient.
     * Uses INITIAL_TRIGGER_ENTER so that if the device is already inside any of the geofences at registration time, the entry transition fires immediately.
     * If triggered, GeofenceBroadcastReceiver marks both the overlapping report and newReport as confirmed in Firebase.
     *
     * @param newReport The newly submitted report to check confirmation against.
     */
    private fun registerConfirmationGeofences(newReport: Report) {
        // check if reports are of the same type, and isn't the same report.
        val sameTypeReports = reportViewModel.reports.value.filter {
            it.type == newReport.type && it.uid != newReport.uid
        }
        // else return, only need to check geofence radius if reports of same types exist
        if (sameTypeReports.isEmpty()) return

        // create geofence per report
        val geofenceList = sameTypeReports.map { report ->
            Geofence.Builder()
                .setRequestId(report.uid) // set ID of geofence to ID of report
                .setCircularRegion(report.latitude, report.longitude, GeofenceBroadcastReceiver.GEOFENCE_RADIUS_METERS) // radius around report point
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS) // geofence gets automatically removed after period ends
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER) // track entry
                .build()
        }

        // register how related geofences are triggered
        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER) // triggers if the device is already inside the geofence
            .addGeofences(geofenceList)
            .build()

        // starts the broadcastReciever which gets updates when geofence is triggered
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
            .putExtra(GeofenceBroadcastReceiver.EXTRA_NEW_REPORT_UID, newReport.uid)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // if have necessary permissions, add geofence
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getGeofencingClient(requireActivity())
                .addGeofences(request, pendingIntent)
                .addOnSuccessListener { Log.d(TAG, "Geofences registered for confirmation check") }
                .addOnFailureListener { Log.e(TAG, "Failed to register geofences: ${it.message}") }
        }
    }
}