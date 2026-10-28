
package com.example.recordatoriosapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Conexión local mediante ADB reverse
    private const val BASE_URL = "http://127.0.0.1:3000/api/"

    // Registro básico de peticiones durante las pruebas
    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val clienteOkHttp = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(clienteOkHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: RecordatoriosApiService =
        retrofit.create(RecordatoriosApiService::class.java)
}
