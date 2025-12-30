package com.example.aladeenadmin.data.repository

import android.content.Context
import com.example.aladeenadmin.data.api.RetrofitClient
import com.example.aladeenadmin.data.model.AdminLoginRequest
import com.example.aladeenadmin.data.model.AdminLoginResponse

class AdminAuthRepository(private val context: Context) {

    suspend fun login(username: String, password: String): AdminLoginResponse {
        val request = AdminLoginRequest(username, password)
        return RetrofitClient.getApiService(context).loginAdmin(request)
    }
}
