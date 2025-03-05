package scisrc.mobiledev.blogexample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import scisrc.mobiledev.blogexample.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_DELAY = 2000L // 2 seconds delay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar if it exists
        supportActionBar?.hide()

        // Delay for splash screen visibility and perform checks
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserLoginStatus()
        }, SPLASH_DELAY)
    }

    private fun checkUserLoginStatus() {
        // Check Firebase Auth first (if you're using Firebase)
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // User is already authenticated with Firebase, go to main activity
            navigateToMainActivity()
            return
        }

        // Check SharedPreferences as backup (or as primary if not using Firebase)
        val sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            // Check if login credentials are valid or token is still valid
            // This is where you'd verify a stored token if you're using token-based auth

            // For this example, we're assuming the stored login is valid
            navigateToMainActivity()
        } else {
            // User is not logged in, go to login screen
            navigateToLoginActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close splash activity so it's not in the back stack
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close splash activity so it's not in the back stack
    }
}