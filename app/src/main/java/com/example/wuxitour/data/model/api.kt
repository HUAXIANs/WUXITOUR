package com.example.wuxitour.data.model
// 内容与上次相同，确保统一
data class LoginRequest(val username: String, val passowrd: String)
data class LoginResponse(val token: String, val user: User)
data class RegisterRequest(val username: String, val passowrd: String, val email: String)