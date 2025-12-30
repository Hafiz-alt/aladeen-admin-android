package com.example.aladeenadmin.data.model

import com.google.gson.annotations.SerializedName

data class UPIAccount(
    val id: Int? = null,
    @SerializedName("upi_id")
    val upiId: String,
    @SerializedName("qr_code")
    val qrCode: String?,
    @SerializedName("is_active")
    val isActive: Boolean = false
)
