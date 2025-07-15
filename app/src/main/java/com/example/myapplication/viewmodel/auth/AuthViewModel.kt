package com.example.myapplication.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.AuthApiService
import com.example.myapplication.model.LoginRequest
import com.example.myapplication.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authApiService: AuthApiService
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authApiService.login(LoginRequest(email, password))
                _user.value = response.user
                // Optional: 儲存 response.token 到 DataStore
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login error: ${e.message}")
            }
        }
    }
}
