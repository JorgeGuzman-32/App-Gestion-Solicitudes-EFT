package com.example.gestionsolicitudes.data.repository

import com.example.gestionsolicitudes.data.local.SolicitudDao
import com.example.gestionsolicitudes.data.local.SolicitudEntity
import com.example.gestionsolicitudes.data.remote.ApiService
import com.example.gestionsolicitudes.data.remote.ServicioExternoModel
import kotlinx.coroutines.flow.Flow

class SolicitudRepository(
    private val solicitudDao: SolicitudDao,
    private val apiService: ApiService? = null
) {
    val todasLasSolicitudes: Flow<List<SolicitudEntity>> = solicitudDao.getAllSolicitudes()

    fun getAllSolicitudes(): Flow<List<SolicitudEntity>> = solicitudDao.getAllSolicitudes()

    fun getSolicitudesByCliente(clienteId: String): Flow<List<SolicitudEntity>> =
        solicitudDao.getSolicitudesByCliente(clienteId)

    fun getSolicitudesByTecnico(tecnicoId: String): Flow<List<SolicitudEntity>> =
        solicitudDao.getSolicitudesByTecnico(tecnicoId)

    suspend fun getSolicitudById(id: Long): SolicitudEntity? = solicitudDao.getSolicitudById(id)

    suspend fun insertSolicitud(solicitud: SolicitudEntity): Long {
        val id = solicitudDao.insertSolicitud(solicitud)
        
        // Simular intento de conexión al servicio Spring Boot
        try {
            apiService?.enviarSolicitud(solicitud.copy(id = id))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return id
    }

    suspend fun guardarSolicitud(solicitud: SolicitudEntity): Long = insertSolicitud(solicitud)

    suspend fun updateSolicitud(solicitud: SolicitudEntity) = solicitudDao.updateSolicitud(solicitud)
    
    // Función para obtener catálogo de Spring Boot (API Externa)
    suspend fun getCatalogoServiciosExternos(): List<ServicioExternoModel> {
        return try {
            val response = apiService?.getCatalogoServicios()
            if (response?.isSuccessful == true) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList() // Si no hay conexión o el Spring Boot está apagado, retorna lista vacía
        }
    }
}
