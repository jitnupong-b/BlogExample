package scisrc.mobiledev.blogexample

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import scisrc.mobiledev.blogexample.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val db = FirebaseFirestore.getInstance()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.cbRememberMe.isChecked = false
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        // Set up login button click listener
        binding.btnLogin.setOnClickListener {
            if (validateInputs()) {
                performLogin()
            }
        }

        // Set up signup text click listener
        binding.tvSignup.setOnClickListener {
            // Navigate to Signup Activity
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

    }

    private fun validateInputs(): Boolean {
        var isValid = true

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
            binding.tilPassword.error = "Please enter your password"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

    private fun performLogin() {
        // Show loading indicator
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Logging in..."
        binding.progressBar.visibility = View.VISIBLE

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // Query Firestore to check if user exists with provided email and password
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE

                if (documents.isEmpty) {
                    // No user found with this email
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = getString(R.string.login)
                    Toast.makeText(this, "No account found with this email", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // User found, check password
                val userDocument = documents.documents[0]
                val storedPassword = userDocument.getString("password")

                if (storedPassword == password) {
                    // Password matches - login successful
                    val userId = userDocument.id
                    val userFullName = userDocument.getString("fullName") ?: ""

                    if(binding.cbRememberMe.isChecked) {
                        // Initialize SharedPreferences
                        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                        editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putString("userId", userId)
                        editor.putString("userFullName", userFullName)
                        editor.apply()
                    } else {
                        // Clear SharedPreferences
                        editor.clear()
                        editor.apply()
                    }

                    // Navigate to Main Activity
                    Toast.makeText(this, "Welcome $userFullName!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Password doesn't match
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = getString(R.string.login)
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Query failed
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = getString(R.string.login)
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}