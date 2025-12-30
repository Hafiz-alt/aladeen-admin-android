package com.example.aladeenadmin.data.repository

import android.content.Context
import com.example.aladeenadmin.data.api.RetrofitClient
import com.example.aladeenadmin.data.model.Snack

class MenuRepository(private val context: Context) {

    suspend fun getMenu(): List<Snack> {
        return RetrofitClient.getApiService(context).getMenu()
    }

    suspend fun updateSnack(id: Int, updates: Map<String, Any>): Snack {
        return RetrofitClient.getApiService(context).updateSnack(id, updates)
    }
}
