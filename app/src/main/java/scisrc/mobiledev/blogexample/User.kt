package scisrc.mobiledev.blogexample

import com.google.firebase.Timestamp
import com.google.firebase.database.ServerValue

data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val createdAt: Any = ServerValue.TIMESTAMP
)

data class Post(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val author: String = "",
    val timestamp: Timestamp ?= null,
    val imageUrl: String = ""
)