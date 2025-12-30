package com.example.aladeenadmin.data.repository

import android.content.Context
import com.example.aladeenadmin.data.api.RetrofitClient
import com.example.aladeenadmin.data.model.UPIAccount
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UPIRepository(private val context: Context) {

    suspend fun getUPIAccounts(): List<UPIAccount> {
        return RetrofitClient.getApiService(context).getUPIAccounts()
    }

    suspend fun createUPIAccount(upiId: String, qrCode: MultipartBody.Part?): UPIAccount {
        val mediaType = MediaType.parse("text/plain")
        val upiIdBody = RequestBody.create(mediaType, upiId)

        // If no QR code is provided, create a valid 1x1 transparent PNG placeholder
        // This avoids "empty file" or "invalid image" errors from the backend.
        val finalQrPart = qrCode ?: run {
            val transparentPng = byteArrayOf(
                0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(), 0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x0D.toByte(), 0x49.toByte(), 0x48.toByte(), 0x44.toByte(), 0x52.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(),
                0x08.toByte(), 0x06.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x1F.toByte(), 0x15.toByte(), 0xC4.toByte(),
                0x89.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x0A.toByte(), 0x49.toByte(), 0x44.toByte(), 0x41.toByte(),
                0x54.toByte(), 0x78.toByte(), 0x9C.toByte(), 0x63.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte(), 0x00.toByte(),
                0x05.toByte(), 0x00.toByte(), 0x01.toByte(), 0x0D.toByte(), 0x0A.toByte(), 0x2D.toByte(), 0xB4.toByte(), 0x00.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x49.toByte(), 0x45.toByte(), 0x4E.toByte(), 0x44.toByte(), 0xAE.toByte(),
                0x42.toByte(), 0x60.toByte(), 0x82.toByte()
            )
            val requestBody = RequestBody.create(MediaType.parse("image/png"), transparentPng)
            MultipartBody.Part.createFormData("qr_code", "placeholder.png", requestBody)
        }

        return RetrofitClient.getApiService(context).createUPIAccount(upiIdBody, finalQrPart)
    }

    suspend fun activateUPIAccount(id: Int) {
        RetrofitClient.getApiService(context).activateUPIAccount(id)
    }
}
