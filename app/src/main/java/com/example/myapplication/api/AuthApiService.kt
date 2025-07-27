package com.example.myapplication.api

import com.example.myapplication.model.LoginRequest
import com.example.myapplication.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

//    // 未來如果有註冊功能
//    @POST("auth/register")
//    suspend fun register(@Body request: RegisterRequest): LoginResponse
}