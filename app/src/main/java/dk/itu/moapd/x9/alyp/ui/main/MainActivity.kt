package dk.itu.moapd.x9.alyp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.color.DynamicColors
import dk.itu.moapd.x9.alyp.R
import dk.itu.moapd.x9.alyp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val reports = mutableListOf<Report>()

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // dropdown menu for report types
        val types = resources.getStringArray(R.array.report_types_array)
        ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            types
        ).also { adapter ->
            binding.reportTypeInput.setAdapter(adapter)
        }

        // format Date
        binding.reportDateTimeInput.setText(dateFormatter.format(Date()))
        val dateTimeText = binding.reportDateTimeInput.text.toString().trim()
        val reportDate = dateFormatter.parse(dateTimeText) ?: Date()


        binding.submitBtn.setOnClickListener { view: View ->
            if(!validateInput()) return@setOnClickListener

            val checkedId = binding.buttonToggleGroup.checkedButtonId

            if(checkedId == View.NO_ID) {
                Toast.makeText(this, "Please select a severity level", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val severity = when (checkedId) {
                R.id.button_minor -> "Minor"
                R.id.button_moderate -> "Moderate"
                R.id.button_major -> "Major"
                else -> "Unknown"
            }

            val report = Report(
                id = UUID.randomUUID(),
                title = binding.reportTitleInput.text.toString().trim(),
                location = binding.reportLocationInput.text.toString().trim(),
                date = reportDate,
                type = binding.reportTypeInput.text.toString().trim(),
                description = binding.reportDescriptionInput.text.toString().trim(),
                severity = severity
            )
            reports.add(report)
            Toast.makeText(this, report.toString(), Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Report stored, Success!")
        }
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

        if(reportTitle.isBlank()) {binding.reportTitleLayout.error = "Required"; ok = false }
        if(reportLocation.isBlank()) {binding.reportLocationLayout.error = "Required"; ok = false }
        if(reportDate.isBlank()) {binding.reportDateLayout.error = "Required"; ok = false }
        if(reportType.isBlank()) {binding.reportTypeLayout.error = "Required"; ok = false }
        if(reportDescription.isBlank()) {binding.reportDescriptionLayout.error = "Required"; ok = false }

        return ok
    }
}