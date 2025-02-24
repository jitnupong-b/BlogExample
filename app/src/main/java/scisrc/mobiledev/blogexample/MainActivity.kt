package scisrc.mobiledev.blogexample

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import scisrc.mobiledev.blogexample.databinding.ActivityMainBinding
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PostAdapter
    private lateinit var database: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        database = Firebase.firestore

        // Setup RecyclerView
        setupRecyclerView()

        // Set click listener for FAB
        binding.fabAddPost.setOnClickListener {
            showAddPostDialog()
        }

        // Setup SwipeRefreshLayout
        binding.swipeRefresh.setOnRefreshListener {
            readPosts()
        }

        // Read data from Firebase
        readPosts()
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(
            onPostClick = { post ->
                // Handle post click (view details)
                showPostDetails(post)
            },
            onEditClick = { post ->
                // Handle edit click
                showEditDialog(post)
            },
            onDeleteClick = { post ->
                // Handle delete click
                showDeleteConfirmation(post)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun showPostDetails(post: Post) {
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra("post_id", post.id)
        })
    }

    private fun showEditDialog(post: Post) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_post, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etContent = dialogView.findViewById<EditText>(R.id.etContent)
        val etAuthor = dialogView.findViewById<EditText>(R.id.etAuthor)

        // Pre-fill existing data
        etTitle.setText(post.title)
        etContent.setText(post.content)
        etAuthor.setText(post.author)

        AlertDialog.Builder(this)
            .setTitle("Edit Post")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val updatedPost = post.copy(
                    title = etTitle.text.toString(),
                    content = etContent.text.toString(),
                    author = etAuthor.text.toString()
                )
                updatePost(updatedPost)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(post: Post) {
        AlertDialog.Builder(this)
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { dialog, _ ->
                deletePost(post.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updatePost(post: Post) {
        showLoading(true)

        database.collection("blog_posts")
            .whereEqualTo("id", post.id)  // Query by id field
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Get the first document (should be only one)
                val document = documents.documents[0]

                // Update the document
                document.reference.update(
                    mapOf(
                        "title" to post.title,
                        "content" to post.content,
                        "author" to post.author,
                        "timestamp" to Timestamp.now()
                    )
                ).addOnSuccessListener {
                    Toast.makeText(this, "Post updated successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update post: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error finding post: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        showLoading(false)
    }

    private fun deletePost(postId: String) {
        showLoading(true)

        database.collection("blog_posts")
            .whereEqualTo("id", postId)  // Query by id field
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Get the first document (should be only one)
                val document = documents.documents[0]

                // Delete the document
                document.reference.delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to delete post: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error finding post: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        showLoading(false)
    }

    private fun readPosts() {
        // ßshowLoading(true)

        database.collection("blog_posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->

                // Make sure to stop the refreshing animation
                binding.swipeRefresh.isRefreshing = false

                if (e != null) {
                    Toast.makeText(this, "Error loading posts: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val postsList = mutableListOf<Post>()
                    for (doc in snapshot.documents) {
                        val post = doc.toObject(Post::class.java)
                        post?.let { postsList.add(it) }
                    }
                    adapter.updatePosts(postsList)
                    updateEmptyState(postsList.isEmpty())
                }
            }
    }

    // Helper function to update empty state
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showAddPostDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_post, null)

        // Get references to views
        val etTitle = dialogView.findViewById<TextInputEditText>(R.id.etTitle)
        val etContent = dialogView.findViewById<TextInputEditText>(R.id.etContent)
        val etAuthor = dialogView.findViewById<TextInputEditText>(R.id.etAuthor)

        AlertDialog.Builder(this)
            .setTitle("Add New Post")
            .setView(dialogView)
            .setPositiveButton("Post") { dialog, _ ->
                // Validate inputs
                val title = etTitle.text.toString().trim()
                val content = etContent.text.toString().trim()
                val author = etAuthor.text.toString().trim()

                if (title.isEmpty() || content.isEmpty() || author.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Add new post
                addNewPost(title, content, author)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addNewPost(title: String, content: String, author: String) {
        // Show loading indicator
        showLoading(true)

        // Create post object
        val post = hashMapOf(
            "id" to UUID.randomUUID().toString(), // or you can use DocumentReference id later
            "title" to title,
            "content" to content,
            "author" to author,
            "timestamp" to FieldValue.serverTimestamp()
        )

        database.collection("blog_posts")
            .add(post)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(this, "Post added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                showLoading(false)
                Toast.makeText(this, "Failed to add post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}