package dk.itu.moapd.x9.alyp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.x9.alyp.databinding.FragmentReportFormBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.getValue

private const val TAG = "ReportFormFragment"

class ReportFormFragment : Fragment() {
    private var _binding: FragmentReportFormBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    val reportViewModel: ReportViewModel by activityViewModels()
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

        binding.submitBtn.setOnClickListener { view: View ->
           submitReport()
        }
    }

    private fun submitReport() {
        if(!validateInput()) return

        val checkedId = binding.buttonToggleGroup.checkedButtonId

        if (checkedId == View.NO_ID) {
            Toast.makeText(requireContext(), "Please select a severity level", Toast.LENGTH_SHORT).show()
            return
        }

        val severity = when (checkedId) {
            R.id.button_minor -> "Minor"
            R.id.button_moderate -> "Moderate"
            R.id.button_major -> "Major"
            else -> "Unknown"
        }

        // parse date at submit time
        val dateTimeText = binding.reportDateTimeInput.text.toString().trim()
        val reportDate = dateFormatter.parse(dateTimeText) ?: Date()

        val report = Report(
            id = UUID.randomUUID(),
            title = binding.reportTitleInput.text.toString().trim(),
            location = binding.reportLocationInput.text.toString().trim(),
            date = reportDate,
            type = binding.reportTypeInput.text.toString().trim(),
            description = binding.reportDescriptionInput.text.toString().trim(),
            severity = severity
        )

        reportViewModel.addReport(report)
        Log.d(TAG, "Report stored, Success!")

        Toast.makeText(requireContext(), "Report stored successfully!", Toast.LENGTH_SHORT).show()

        findNavController().navigateUp()
    }

    private fun validateInput(): Boolean {
        val reportTitle = binding.reportTitleInput.text?.toString()?.trim().orEmpty()
        val reportLocation = binding.reportLocationInput.text?.toString()?.trim().orEmpty()
        val reportDate = binding.reportDateTimeInput.text?.toString()?.trim().orEmpty()
        val reportType = binding.reportTypeInput.text?.toString()?.trim().orEmpty()
        val reportDescription = binding.reportDescriptionInput.text?.toString()?.trim().orEmpty()

        binding.reportTitleLayout.error = null
        binding.reportLocationLayout.error = null
        binding.reportDateLayout.error = null
        binding.reportTypeLayout.error = null
        binding.reportDescriptionLayout.error = null

        var ok = true

        if (reportTitle.isBlank()) {
            binding.reportTitleLayout.error = "Required"; ok = false
        }
        if (reportLocation.isBlank()) {
            binding.reportLocationLayout.error = "Required"; ok = false
        }
        if (reportDate.isBlank()) {
            binding.reportDateLayout.error = "Required"; ok = false
        }
        if (reportType.isBlank()) {
            binding.reportTypeLayout.error = "Required"; ok = false
        }
        if (reportDescription.isBlank()) {
            binding.reportDescriptionLayout.error = "Required"; ok = false
        }

        return ok
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