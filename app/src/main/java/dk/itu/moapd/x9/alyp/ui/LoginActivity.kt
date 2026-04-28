package dk.itu.moapd.x9.alyp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.alyp.R

/**
 * Entry point of the app responsible for authenticating the user via Firebase UI.
 *
 * Launches the Firebase UI sign-in screen immediately on creation. Email, phone, and Google sign-in options.
 * On successful authentication the user is navigated to MainActivity. On failure, the activity relaunches.
 *
 * Inspired by Firebase documentation for 'Sign in with a pre-built UI' https://firebase.google.com/docs/auth/android/firebaseui
 */
private const val TAG ="LoginActivity"

class LoginActivity : AppCompatActivity() {
    /**
     * Launcher for the Firebase UI sign-in flow.
     * Delivers the authentication result to onSignInResult.
     */
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { result ->
        this.onSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate(Bundle?) called")
        createSignInIntent()
    }

    /**
     * Builds and launches the Firebase UI sign-in intent with email,
     * phone, and Google as available authentication providers.
     */
    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.AppTheme)
            .build()
        signInLauncher.launch(signInIntent)
    }

    /**
     * Handles results from sign-in attempt, on success retrieve the current user, display a success text and navigate to MainActivity.
     * On failed sign-in attempt, navigate back to sign-in screen.
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "${user?.email} successfully logged in", Toast.LENGTH_SHORT).show()
            startMainActivity()
        } else {
            Toast.makeText(this, "Sign-in failed: ${result.idpResponse?.error?.errorCode}", Toast.LENGTH_SHORT).show()
            createSignInIntent()
        }
    }

    private fun startMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}