package com.example.myapplication.model

import java.time.LocalDate
import java.time.Period

data class User(
    val id: String,
    val username: String,
    val email: String,
    val mbti: String,
    val birthday: LocalDate,
    val phoneNumber: String,
    val bio: String? = null,
) {
    fun getAge(): Int {
        return Period.between(birthday, LocalDate.now()).years
    }
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val user: User,
    val token: String
)
