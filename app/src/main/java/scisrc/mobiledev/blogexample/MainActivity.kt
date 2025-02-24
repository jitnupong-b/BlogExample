package scisrc.mobiledev.blogexample

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.database

class MainActivity : AppCompatActivity() {
    private val database = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // setValue() - Direct Write
    fun directWrite() {
        // Writing user data at a specific path
        val userId = "user123"
        val user = User(
            name = "John Doe",
            email = "john@example.com",
            age = 25
        )

        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "User data written successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error writing user data", e)
            }
    }

    // push() - Generate Unique Keys
    fun pushData() {
        // Creating a new post with auto-generated ID
        val postData = Post(
            title = "My Post",
            content = "Post content",
            timestamp = ServerValue.TIMESTAMP
        )

        val postsRef = database.child("posts")
        val newPostRef = postsRef.push()
        newPostRef.setValue(postData)
            .addOnSuccessListener {
                val postId = newPostRef.key // Get the auto-generated key
                Log.d(TAG, "Post created with ID: $postId")
            }
    }

    fun batchWrite(userId: String, postId: String, postData: Post) {
        val database = Firebase.database.reference

        // Create a map for batch update
        val updates = hashMapOf<String, Any>(
            "/users/$userId/lastLogin" to ServerValue.TIMESTAMP,
            "/posts/$postId/likes" to mapOf(userId to true),
            "/user-posts/$userId/$postId" to postData,
            "/post-likes/$postId/$userId" to true
        )

        // Perform atomic update
        database.updateChildren(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Batch update successful")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Batch update failed", e)
            }
    }

    // Example of batch write with custom objects
    fun createNewPost(userId: String, postContent: String) {
        val postId = database.child("posts").push().key ?: return

        val post = Post(
            userId = userId,
            content = postContent,
            timestamp = ServerValue.TIMESTAMP
        )

        val updates = hashMapOf<String, Any>(
            "/posts/$postId" to post,
            "/user-posts/$userId/$postId" to post,
            "/users/$userId/postCount" to ServerValue.increment(1)
        )

        database.updateChildren(updates)
    }
}