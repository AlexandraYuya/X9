package dk.itu.moapd.x9.alyp.ui

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.alyp.R
import dk.itu.moapd.x9.alyp.databinding.FragmentReportFormBinding
import dk.itu.moapd.x9.alyp.model.Report
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

private const val TAG = "ReportFormFragment"

/**
 * ReportFormFragment represents the second fragment after the list fragment.
 * Determines the input for the form fields.
 * Validates input upon submitting before storing the new Report object.
 * Navigates back to ReportListFragment upon successful submitting.
 */
class ReportFormFragment : Fragment() {
    private var _binding: FragmentReportFormBinding? = null
    private lateinit var auth: FirebaseAuth
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    val reportViewModel: ReportViewModel by activityViewModels()
    val cameraViewModel: CameraViewModel by activityViewModels()
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentReportFormBinding.inflate(inflater, container, false).also {
        _binding = it
        auth = FirebaseAuth.getInstance()
    }.root

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
        binding.reportCamera.setOnClickListener { view: View ->
            // launch camera fragment if has permission
            findNavController().navigate(R.id.action_reportFormFragment_to_reportCameraFragment)
        }

        binding.submitBtn.setOnClickListener { view: View ->
            if (auth.currentUser != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    submitReport()
                }
            }else {
                Toast.makeText(context, "Log-in to submit a report", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
            description = binding.reportDescriptionInput.text.toString().trim(),
            severity = severity,
            user = auth.currentUser?.email.toString(),
            imageUrl = cameraViewModel.imageUri.value.toString()
        )

        reportViewModel.addUserReport(report)
        Log.d(TAG, "Report stored, Success!")

        Toast.makeText(requireContext(), "Report stored successfully!", Toast.LENGTH_SHORT).show()

        findNavController().navigate(R.id.action_reportFormFragment_to_reportListFragment)
    }

    private fun validateInput(): Boolean {
        with(binding) {
            reportTitleLayout.error = null
            reportLocationLayout.error = null
            reportDateLayout.error = null
            reportTypeLayout.error = null
            reportDescriptionLayout.error = null

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
            if (reportDescriptionInput.text?.toString()?.trim().orEmpty().isBlank()) {
                binding.reportDescriptionLayout.error = getString(R.string.error_required)
                ok = false
            }

            return ok
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}