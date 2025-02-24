package scisrc.mobiledev.blogexample

import com.google.firebase.database.ServerValue

data class User(
    val name: String = "",
    val email: String = "",
    val age: Int = 0,
    val createdAt: Any = ServerValue.TIMESTAMP
)

data class Post(
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val category: String = "",
    val timestamp: Any = ServerValue.TIMESTAMP,
    val likes: Int = 0
)