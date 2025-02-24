package scisrc.mobiledev.blogexample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import scisrc.mobiledev.blogexample.databinding.BlogItemViewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private val posts = mutableListOf<Post>()

    class PostViewHolder(private val binding: BlogItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                postTitle.text = post.title
                postContent.text = post.content
                postTimeStamp.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(Date(post.timestamp.toString()))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = BlogItemViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}