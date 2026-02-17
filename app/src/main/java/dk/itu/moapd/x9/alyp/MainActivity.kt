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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
     * Use adapter to populate list from list of reports
      */
    private val cheatLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            val report = result.data?.getSerializableExtra(EXTRA_REPORT_DATA) as? Report

            if (report != null) {
                reportViewModel.addReport(report)
                Toast.makeText(this, "report stored successfully!", Toast.LENGTH_LONG).show()
            }else {
                Toast.makeText(this, "No report stored", Toast.LENGTH_LONG).show()
            }

//            ArrayAdapter(
//                this,
//                R.layout.simple_list_item_1,
//                reportViewModel.getReportList
//            ).also { adapter ->
////                binding.fragmentContainer.setAdapter(adapter)
//            }
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

//        binding.listView.setOnItemClickListener { parent, view, position, id ->
//            val item = parent.getItemAtPosition(position)
//            ReportFragment().show(supportFragmentManager, "REPORT_DIALOG")
//        }
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