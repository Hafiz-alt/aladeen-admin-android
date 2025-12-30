package com.example.aladeenadmin.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,
    @SerializedName("lab_name")
    val labName: String,
    @SerializedName("system_number")
    val systemNumber: String,
    @SerializedName("total_amount")
    val totalAmount: String,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String
)
