package com.example.gestionsolicitudes.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // IMPORTANTE: Ajustar URL base dependiendo de donde ejecutes el backend (Spring Boot)
    // Para emulador y Spring Boot local: http://10.0.2.2:8080/
    // Para dispositivo físico: IP de la red WiFi (ej. http://192.168.1.100:8080/)
    private const val BASE_URL = "http://10.0.2.2:8081/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}