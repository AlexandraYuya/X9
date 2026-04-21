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
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private val reportViewModel : ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")

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

    // inflate the toolbar's menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_app_bar, menu)
        return true
    }

    // configure paths/actions for appbar navigation
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

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    private fun startMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}