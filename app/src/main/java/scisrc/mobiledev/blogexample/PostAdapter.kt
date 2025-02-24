package scisrc.mobiledev.blogexample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter( private val onPostClick: (Post) -> Unit,
                   private val onEditClick: (Post) -> Unit,
                   private val onDeleteClick: (Post) -> Unit) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val posts = mutableListOf<Post>()

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.blog_item_view, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // Bind data to views
        holder.apply {
            tvTitle.text = post.title
            tvContent.text = post.content
            tvAuthor.text = "By ${post.author}"
            tvDate.text = post.timestamp?.let {
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(it.toDate())
            } ?: "No date"

            // Item click listener
            itemView.setOnClickListener { onPostClick(post) }

            // Edit button click listener
            btnEdit.setOnClickListener { onEditClick(post) }

            // Delete button click listener
            btnDelete.setOnClickListener { onDeleteClick(post) }
        }
    }

    override fun getItemCount() = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}