package com.example.aladeenadmin.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aladeenadmin.data.api.RetrofitClient
import com.example.aladeenadmin.data.model.DeviceRegistrationRequest
import com.example.aladeenadmin.data.repository.AdminAuthRepository
import com.example.aladeenadmin.utils.TokenManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
                
                // Register device for push notifications
                registerDevice()
                
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

    private suspend fun registerDevice() {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            RetrofitClient.getApiService(getApplication()).registerDevice(
                DeviceRegistrationRequest(deviceToken = token)
            )
        } catch (e: Exception) {
            // Log error or handle failure silently as login is primary goal
            e.printStackTrace()
        }
    }
}
