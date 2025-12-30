package com.example.aladeenadmin.data.api

import android.content.Context
import com.example.aladeenadmin.utils.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var retrofit: Retrofit? = null

    fun getApiService(context: Context): AdminApiService {
        if (retrofit == null) {
            val tokenManager = TokenManager(context)
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenManager))
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(AdminApiService.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(AdminApiService::class.java)
    }
}
