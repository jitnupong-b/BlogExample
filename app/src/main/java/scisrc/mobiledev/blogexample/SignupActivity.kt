package scisrc.mobiledev.blogexample

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import scisrc.mobiledev.blogexample.databinding.ActivitySignupBinding
import java.util.UUID

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Initialize Firebase
        database = Firebase.firestore

        setupViews()
    }

    private fun setupViews() {
        // Set up signup button click listener
        binding.btnSignup.setOnClickListener {
            if (validateInputs()) {
                // Perform signup operation
                performSignup()
            }
        }

        // Set up login text click listener
        binding.tvLogin.setOnClickListener {
            // Navigate to Login Activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate full name
        val fullName = binding.etFullName.text.toString().trim()
        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Please enter your full name"
            isValid = false
        } else {
            binding.tilFullName.error = null
        }

        // Validate email
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.tilEmail.error = "Please enter your email"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email address"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        // Validate password
        val password = binding.etPassword.text.toString()
        if (password.isEmpty()) {
            binding.tilPassword.error = "Please enter a password"
            isValid = false
        } else if (password.length < 8) {
            binding.tilPassword.error = "Password must be at least 8 characters"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        // Validate confirm password
        val confirmPassword = binding.etConfirmPassword.text.toString()
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        // Validate terms checkbox
        if (!binding.cbTerms.isChecked) {
            Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun performSignup() {
        // Show loading indicator
        showLoading(true)

        val fullName = binding.etFullName.text
        val email = binding.etEmail.text
        val password = binding.etPassword.text

        // Create post object
        val user = hashMapOf(
            "id" to UUID.randomUUID().toString(), // or you can use DocumentReference id later
            "fullName" to fullName,
            "email" to email,
            "password" to password,
            "timestamp" to FieldValue.serverTimestamp()
        )

        database.collection("users")
            .add(user)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(this, "User Registration successfully", Toast.LENGTH_SHORT).show()

                // Navigate to Main Activity or Login Activity
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                showLoading(false)
                Toast.makeText(this, "Failed to add new user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading(show: Boolean) {
        binding.signUpProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

}