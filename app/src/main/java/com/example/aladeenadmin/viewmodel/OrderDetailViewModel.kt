package com.example.aladeenadmin.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aladeenadmin.data.model.OrderDetail
import com.example.aladeenadmin.data.repository.OrdersRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OrderDetailViewModel(
    application: Application,
    private val orderId: Int
) : AndroidViewModel(application) {

    private val repository = OrdersRepository(application)

    var orderDetail by mutableStateOf<OrderDetail?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isApproving by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var approveSuccess by mutableStateOf(false)
        private set

    init {
        fetchOrderDetail()
    }

    private fun fetchOrderDetail() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                orderDetail = repository.getOrderDetail(orderId)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                errorMessage = "Detail Error (${e.code()}): $errorBody"
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to fetch order details"
            } finally {
                isLoading = false
            }
        }
    }

    fun approvePayment(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isApproving = true
            errorMessage = null
            try {
                repository.approvePayment(orderId)
                approveSuccess = true
                onSuccess()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                if (e.code() == 400 && errorBody?.contains("already approved") == true) {
                    errorMessage = "This payment has already been approved."
                    // Force refresh or back
                    onSuccess() 
                } else {
                    errorMessage = "Approve Error (${e.code()}): $errorBody"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to approve payment"
            } finally {
                isApproving = false
            }
        }
    }
}
