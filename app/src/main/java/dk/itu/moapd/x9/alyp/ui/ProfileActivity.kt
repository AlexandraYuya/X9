package dk.itu.moapd.x9.alyp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.alyp.R
import dk.itu.moapd.x9.alyp.databinding.ActivityProfileBinding
import dk.itu.moapd.x9.alyp.viewmodel.ReportViewModel

private const val TAG = "ProfileActivity"

/**
 * ProfileActivity displays the current user's submitted reports in a list.
 * Allows the user to swipe to delete their own reports.
 * MainActivity and ProfileActivity reuses the ReportListFragment. ProfileActivity uses ReportListFragment with allowDelete enabled.
 */
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private val reportViewModel : ReportViewModel by viewModels()

    /**
     * Initializes the activity, loads the current user's reports if signed-in.
     * Adds ReportListFragment with swipe to delete enabled.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")

        auth = FirebaseAuth.getInstance()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if(auth.currentUser != null) {
            reportViewModel.loadUserReports()
        }

        // only the first time the activity is created, add the fragment. Prevent constant reload when activity is created
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(binding.profileFragmentContainerView.id, ReportListFragment.newInstance(allowDelete = true)).commit()
        }
    }

    /**
     * Inflate the toolbar's menu.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_app_bar, menu)
        return true
    }

    /**
     * Configure paths/actions for toolbar's navigation.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_back -> {
                startMainActivity()
                invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
        }
    }
}