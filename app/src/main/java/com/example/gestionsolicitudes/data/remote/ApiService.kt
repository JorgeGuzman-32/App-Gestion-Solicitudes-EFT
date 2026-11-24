package com.example.gestionsolicitudes.data.remote

import com.example.gestionsolicitudes.data.local.SolicitudEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("servicios")
    suspend fun getCatalogoServicios(): Response<List<ServicioExternoModel>>

    @POST("solicitudes")
    suspend fun enviarSolicitud(@Body solicitud: SolicitudEntity): Response<Void>
}