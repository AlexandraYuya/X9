package dk.itu.moapd.x9.alyp.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Snackbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.color.DynamicColors
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.alyp.service.LocationService
import dk.itu.moapd.x9.alyp.R
import dk.itu.moapd.x9.alyp.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

/**
 * MainActivity The one and only activity in X9 app.
 *  Base activity where the inner content is dynamically changed out with fragment navigation.
 * Uses dynamic colours defined in the themes.xml & colors.xml files.
 * NavHostFragment is the actual fragment managing the screen area.
 * NavController is the host's controller which manages the destinations,
 *  and actions between destinations in nav_graph.xml and sets it up with the bottom navigation menu.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate(Bundle?) called")
        auth = FirebaseAuth.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(
                R.id.fragment_container_view
            ) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragment_report_list,
                R.id.fragment_report_form,
                R.id.fragment_report_map
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        setupNavigation(navController)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Snackbar.make(binding.root, "Location permission denied", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun requestOrStartTracking(
        context: Context,
        onHasPermission: () -> Unit,
        onRequestPermission: () -> Unit,
    ) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) onHasPermission() else onRequestPermission()
    }

    // inflate the toolbar's menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_app_bar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_logout)?.isVisible = auth.currentUser != null
        menu?.findItem(R.id.action_login)?.isVisible = auth.currentUser == null
        menu?.findItem(R.id.action_profile)?.isVisible = auth.currentUser != null
        return super.onPrepareOptionsMenu(menu)
    }

    // configure paths/actions for appbar navigation
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                signOut(this)
                invalidateOptionsMenu()
                true
            }

            R.id.action_login -> {
                startLoginActivity()
                true
            }

            R.id.action_profile -> {
                startProfileActivity()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupNavigation(navController: NavController) {
        binding.contentMain.bottomNavigation.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
        invalidateOptionsMenu()

        requestOrStartTracking(
            this,
            { onHasPermission() },
            { onRequestPermission() }
        )
    }

    private fun onHasPermission() {
        Snackbar.make(binding.root, "Location permission activated", Snackbar.LENGTH_SHORT).show()
    }
    private fun onRequestPermission() {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun startProfileActivity() {
        Intent(this, ProfileActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun signOut(context: Context) {
        auth.signOut()
        Toast.makeText(context, "logged out, reports cleared", Toast.LENGTH_LONG).show()
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