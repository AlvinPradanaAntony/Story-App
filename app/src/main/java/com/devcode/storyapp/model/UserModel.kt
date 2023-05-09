package com.devcode.storyapp.model

data class UserModel(
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean
)
