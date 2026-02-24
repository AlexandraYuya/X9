package dk.itu.moapd.x9.alyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.color.DynamicColors
import dk.itu.moapd.x9.alyp.databinding.ActivityMainBinding
import dk.itu.moapd.x9.alyp.R

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
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
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


        // Access fragment manager
        val fm = supportFragmentManager

        //Access fragment navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view
            ) as NavHostFragment
        val navController = navHostFragment.navController
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