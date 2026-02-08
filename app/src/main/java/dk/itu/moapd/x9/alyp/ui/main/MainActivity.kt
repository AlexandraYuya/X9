package dk.itu.moapd.x9.alyp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dk.itu.moapd.x9.alyp.R
import dk.itu.moapd.x9.alyp.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.submitBtn.setOnClickListener { view: View ->
            val reportTitle = binding.reportTitleInput.getText().toString()
            val reportLocation = binding.reportLocationInput.getText().toString()
            val reportDate = binding.reportDateInput.getText().toString()
            val reportType = binding.reportTypeInput.getText().toString()
            val reportDescription = binding.reportDescriptionInput.getText().toString()
            val severityButton = binding.buttonToggleGroup.checkedButtonId.toString()

            val inputData =
                "report title: $reportTitle \n " +
                        "report location: $reportLocation \n " +
                        "report date: $reportDate \n " +
                        "report type: $reportType \n " +
                        "report description: $reportDescription"
            Toast.makeText(this, inputData, Toast.LENGTH_SHORT).show()

        }
    }
}