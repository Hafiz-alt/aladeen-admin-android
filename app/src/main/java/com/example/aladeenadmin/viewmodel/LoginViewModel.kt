package com.example.aladeenadmin.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aladeenadmin.data.repository.AdminAuthRepository
import com.example.aladeenadmin.utils.TokenManager
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdminAuthRepository(application)
    private val tokenManager = TokenManager(application)

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var loginSuccess by mutableStateOf(false)
        private set

    fun login(username: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.login(username, password)
                
                // Save token on successful login
                tokenManager.saveToken(response.token)
                
                loginSuccess = true
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                errorMessage = "Server Error (${e.code()}): $errorBody"
                loginSuccess = false
            } catch (e: Exception) {
                errorMessage = e.message ?: "An unknown error occurred"
                loginSuccess = false
            } finally {
                isLoading = false
            }
        }
    }
}
