package com.example.myapplication.data.model

object CurrentUser {
    var user: User? = null
    var token: String? = null // ← ✅ 新增這行

    fun isLoggedIn(): Boolean = user != null && !token.isNullOrEmpty()

    fun login(user: User, token: String) {
        this.user = user
        this.token = token
    }

    fun logout() {
        this.user = null
        this.token = null
    }
}