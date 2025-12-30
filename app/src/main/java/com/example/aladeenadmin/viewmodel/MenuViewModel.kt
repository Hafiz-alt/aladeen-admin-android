package com.example.aladeenadmin.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aladeenadmin.data.model.Snack
import com.example.aladeenadmin.data.repository.MenuRepository
import kotlinx.coroutines.launch

class MenuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MenuRepository(application)

    var menuItems by mutableStateOf<List<Snack>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchMenu()
    }

    fun fetchMenu() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                menuItems = repository.getMenu()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to fetch menu"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateSnack(id: Int, price: Int, stock: Int, isAvailable: Boolean) {
        viewModelScope.launch {
            try {
                val updates = mapOf(
                    "price" to price,
                    "stock" to stock,
                    "is_available" to isAvailable
                )
                repository.updateSnack(id, updates)
                fetchMenu() // Refresh the list
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to update snack"
            }
        }
    }
}
