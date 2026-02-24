package dk.itu.moapd.x9.alyp

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.color.DynamicColors
import dk.itu.moapd.x9.alyp.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
//    private val reportViewModel: ReportViewModel by viewModels()
//    companion object {
//        private const val EXTRA_REPORT_DATA = "dk.itu.moapd.x9.alyp.report_data"
//    }

    /**
     * launcher to recieve data from child to parent activity.
     * Use adapter to populate list from list of reports
     *
     * Uncommented as report activity changed to fragment,
     * form fragment populates reportViewModel
     * list fragment observes changes made to the reportViewModel
      */
//    private val cheatLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if(result.resultCode == RESULT_OK) {
//            val report = result.data?.getSerializableExtra(EXTRA_REPORT_DATA) as? Report
//
//            if (report != null) {
//                reportViewModel.addReport(report)
//                Toast.makeText(this, "report stored successfully!", Toast.LENGTH_LONG).show()
//            }else {
//                Toast.makeText(this, "No report stored", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
        Log.d(TAG, "OnCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        Log.d(TAG, "Got a ReportViewModel: $reportViewModel")
//        binding.newReportBtn.setOnClickListener { view ->
//            val intent = Intent(this, ReportActivity::class.java)
//            cheatLauncher.launch(intent)
//        }

        // Access fragment manager
        val fm = supportFragmentManager

        //Access fragment navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view
            ) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)

        setSupportActionBar(binding.toolbar)
        setupNavigation(navController)
    }

    private fun setupNavigation(navController: androidx.navigation.NavController) {
        // Portrait: bottom navigation. Landscape: navigation rail.
        binding.contentMain.bottomNavigation.setupWithNavController(navController)
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