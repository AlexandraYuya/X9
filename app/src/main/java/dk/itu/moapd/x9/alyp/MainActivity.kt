package dk.itu.moapd.x9.alyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.android.material.color.DynamicColors
import com.google.firebase.auth.FirebaseAuth
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

        DynamicColors.applyToActivityIfAvailable(this)
        Log.d(TAG, "OnCreate(Bundle?) called")

        auth = FirebaseAuth.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view
            ) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)
        setupNavigation(navController)
    }

    private fun setupNavigation(navController: androidx.navigation.NavController) {
        binding.contentMain.bottomNavigation.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")

        auth.currentUser ?: startLoginActivity()
    }

    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun signOut() {
        auth.signOut()
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