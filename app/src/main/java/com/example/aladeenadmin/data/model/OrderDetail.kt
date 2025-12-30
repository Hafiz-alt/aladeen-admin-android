package com.example.aladeenadmin.data.model

import com.google.gson.annotations.SerializedName

data class OrderDetail(
    val id: Int,
    @SerializedName("lab_name")
    val labName: String,
    @SerializedName("system_number")
    val systemNumber: String,
    @SerializedName("total_amount")
    val totalAmount: String,
    val status: String,
    @SerializedName("payment_status")
    val paymentStatus: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: String
)
