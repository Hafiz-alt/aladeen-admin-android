package com.example.aladeenadmin.data.api

import com.example.aladeenadmin.data.model.*
import retrofit2.http.*

interface AdminApiService {
    
    // AUTHENTICATION
    @POST("api/admin/login/")
    suspend fun loginAdmin(
        @Body request: AdminLoginRequest
    ): AdminLoginResponse

    // ORDERS
    @GET("api/admin/orders/")
    suspend fun getOrders(): List<Order> 

    @GET("api/admin/orders/{id}/")
    suspend fun getOrderDetail(
        @Path("id") orderId: Int
    ): OrderDetail

    @POST("api/admin/orders/{id}/approve-payment/")
    suspend fun approvePayment(
        @Path("id") orderId: Int
    ): OrderStatusResponse

    companion object {
        const val BASE_URL = "http://10.0.2.2:8000/" 
    }
}
