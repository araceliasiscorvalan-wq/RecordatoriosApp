
package com.example.recordatoriosapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // URL temporal: todavía no tenemos un servidor real
    private const val BASE_URL = "https://example.com/api/"

    // Registro HTTP desactivado
    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE
    }

    // Cliente de conexiones HTTP
    private val clienteOkHttp = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .build()

    // Configuración de Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(clienteOkHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Servicio de la API
    val apiService: RecordatoriosApiService =
        retrofit.create(RecordatoriosApiService::class.java)
}
