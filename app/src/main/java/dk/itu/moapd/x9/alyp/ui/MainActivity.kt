package dk.itu.moapd.x9.alyp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.alyp.R
import dk.itu.moapd.x9.alyp.databinding.ActivityMainBinding
import dk.itu.moapd.x9.alyp.viewmodel.ReportViewModel

/**
 * MainActivity is the base activity where the inner content is dynamically switched out with fragment navigation.
 * NavHostFragment is the actual fragment managing the screen area.
 * NavController is the host's controller which manages the destinations,
 *  and actions between destinations in nav_graph.xml and sets it up with the bottom navigation menu.
 */

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    // The activity's view exists for the entire lifetime of the activity.
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private val reportViewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate(Bundle?) called")
        auth = FirebaseAuth.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        reportViewModel.loadPublicReports()

        val navHostFragment = supportFragmentManager
            .findFragmentById(
                R.id.fragment_container_view
            ) as NavHostFragment

        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragment_report_list,
                R.id.fragment_report_form,
                R.id.fragment_report_map
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.contentMain.bottomNavigation.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_app_bar, menu)
        return true
    }

    /**
     * Controls visibility of menu items based on authentication state.
     * Login is shown when signed out, logout and profile are shown when signed in.
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_logout)?.isVisible = auth.currentUser != null
        menu?.findItem(R.id.action_login)?.isVisible = auth.currentUser == null
        menu?.findItem(R.id.action_profile)?.isVisible = auth.currentUser != null
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * Configure paths/actions for toolbar navigation.
     */
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

    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
    private fun startProfileActivity() {
        Intent(this, ProfileActivity::class.java).apply {
            startActivity(this)
        }
    }
    /**
     * Intentional design decision, when signing out, we don't close our activity, instead we disable the profile access and report publishing.
     */
    private fun signOut(context: Context) {
        auth.signOut()
        Toast.makeText(context, "signed out, user reports are no longer visible", Toast.LENGTH_LONG).show()
    }
}