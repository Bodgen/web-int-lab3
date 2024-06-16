package com.example.myapplication

import java.sql.Date
import java.time.OffsetDateTime

data class User(
    val profileId: String,
    val birthdate: String,
    val email: String,
    val username: String,
    val token: String,
)

data class PostItem (
    val id: String?,
    var content: String,
    var title: String,
    var author: Int?,
    val created_at: String?,
    val updated_at: String?
)