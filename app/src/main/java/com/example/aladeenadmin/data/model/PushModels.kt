package com.example.aladeenadmin.data.model

import com.google.gson.annotations.SerializedName

data class DeviceRegistrationRequest(
    @SerializedName("device_token")
    val deviceToken: String,
    val platform: String = "android"
)

data class DeviceUnregistrationRequest(
    @SerializedName("device_token")
    val deviceToken: String
)
