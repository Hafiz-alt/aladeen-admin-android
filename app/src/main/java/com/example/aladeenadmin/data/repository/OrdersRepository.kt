package com.example.aladeenadmin.data.repository

import android.content.Context
import com.example.aladeenadmin.data.api.RetrofitClient
import com.example.aladeenadmin.data.model.Order
import com.example.aladeenadmin.data.model.OrderDetail
import com.example.aladeenadmin.data.model.OrderStatusResponse

class OrdersRepository(private val context: Context) {

    suspend fun getOrders(): List<Order> {
        return RetrofitClient.getApiService(context).getOrders()
    }

    suspend fun getOrderDetail(orderId: Int): OrderDetail {
        return RetrofitClient.getApiService(context).getOrderDetail(orderId)
    }

    suspend fun approvePayment(orderId: Int): OrderStatusResponse {
        return RetrofitClient.getApiService(context).approvePayment(orderId)
    }
}
