package dk.itu.moapd.x9.alyp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import dk.itu.moapd.x9.alyp.ReportActivity
import dk.itu.moapd.x9.alyp.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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

            val reportList = arrayListOf<Report>()
            if (report != null) {
                reportList.add(report)
            }

            ArrayAdapter<Report>(
                this,
                android.R.layout.simple_list_item_1,
                reportList
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