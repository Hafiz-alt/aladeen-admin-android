package com.example.aladeenadmin.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aladeenadmin.data.model.Order
import com.example.aladeenadmin.data.repository.OrdersRepository
import kotlinx.coroutines.launch

class OrdersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OrdersRepository(application)

    var orders by mutableStateOf<List<Order>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                orders = repository.getOrders()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to fetch orders"
            } finally {
                isLoading = false
            }
        }
    }
}
