package com.example.aladeenadmin.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aladeenadmin.data.model.UPIAccount
import com.example.aladeenadmin.data.repository.UPIRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UPIViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UPIRepository(application)

    var upiAccounts by mutableStateOf<List<UPIAccount>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchUPIAccounts()
    }

    fun fetchUPIAccounts() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                upiAccounts = repository.getUPIAccounts()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to fetch UPI accounts"
            } finally {
                isLoading = false
            }
        }
    }

    fun createUPIAccount(upiId: String) {
        viewModelScope.launch {
            errorMessage = null
            try {
                // Pass null for qrCode since we are not uploading a file yet
                repository.createUPIAccount(upiId, null)
                fetchUPIAccounts()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                errorMessage = "Create Error (${e.code()}): $errorBody"
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to create UPI account"
            }
        }
    }

    fun activateUPIAccount(id: Int) {
        viewModelScope.launch {
            errorMessage = null
            try {
                repository.activateUPIAccount(id)
                fetchUPIAccounts()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                errorMessage = "Activate Error (${e.code()}): $errorBody"
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to activate UPI account"
            }
        }
    }
}
