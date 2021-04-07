package com.ahmetc.firebase_example_app.data.models

data class User(
    val uid: String,
    val name: String,
    val surname: String,
    val email: String,
    val avatar_url: String?
)