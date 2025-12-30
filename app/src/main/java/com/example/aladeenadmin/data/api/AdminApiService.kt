package com.example.aladeenadmin.data.api

import com.example.aladeenadmin.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    // MENU
    @GET("api/admin/menu/")
    suspend fun getMenu(): List<Snack>

    @PATCH("api/admin/menu/{id}/")
    suspend fun updateSnack(
        @Path("id") snackId: Int,
        @Body updates: Map<String, @JvmSuppressWildcards Any>
    ): Snack

    // UPI
    @GET("api/admin/upi/")
    suspend fun getUPIAccounts(): List<UPIAccount>

    @Multipart
    @POST("api/admin/upi/")
    suspend fun createUPIAccount(
        @Part("upi_id") upiId: RequestBody,
        @Part("qr_code") qr_code: MultipartBody.Part? = null
    ): UPIAccount

    @PATCH("api/admin/upi/{id}/activate/")
    suspend fun activateUPIAccount(
        @Path("id") upiId: Int
    ): Map<String, String>

    // PUSH NOTIFICATIONS
    @POST("api/admin/register-device/")
    suspend fun registerDevice(
        @Body request: DeviceRegistrationRequest
    ): Unit

    @POST("api/admin/unregister-device/")
    suspend fun unregisterDevice(
        @Body request: DeviceUnregistrationRequest
    ): Unit

    companion object {
        const val BASE_URL = "http://10.0.2.2:8000/" 
    }
}
