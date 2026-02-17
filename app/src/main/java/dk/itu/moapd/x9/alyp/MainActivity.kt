package dk.itu.moapd.x9.alyp

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import dk.itu.moapd.x9.alyp.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val reportViewModel: ReportViewModel by viewModels()
    companion object {
        private const val EXTRA_REPORT_DATA = "dk.itu.moapd.x9.alyp.report_data"
    }

    /**
     * launcher to recieve data from child to parent activity.
      */
    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            val report = result.data?.getSerializableExtra(EXTRA_REPORT_DATA) as? Report

            if (report != null) {
                reportViewModel.setReportList(report)
            }

            ArrayAdapter<Report>(
                this,
                R.layout.simple_list_item_1,
                reportViewModel.getReportList
            ).also { adapter ->
                binding.listView.setAdapter(adapter)
            }
            Toast.makeText(this, report?.toString() ?: "No report returned", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "Got a ReportViewModel: $reportViewModel")

        binding.newReportBtn.setOnClickListener { view ->
            val intent = Intent(this, ReportActivity::class.java)
            cheatLauncher.launch(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
}