package com.example.aladeenadmin.data.model

import com.google.gson.annotations.SerializedName

data class Snack(
    val id: Int,
    val name: String,
    val price: Int,
    val stock: Int,
    @SerializedName("is_available")
    val isAvailable: Boolean
)
