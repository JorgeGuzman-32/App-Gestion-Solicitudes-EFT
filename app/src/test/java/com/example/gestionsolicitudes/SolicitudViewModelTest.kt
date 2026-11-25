package com.example.gestionsolicitudes

import android.content.Context
import com.example.gestionsolicitudes.data.model.UsuarioModel
import com.example.gestionsolicitudes.data.repository.SolicitudRepository
import com.example.gestionsolicitudes.ui.solicitud.SolicitudViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SolicitudViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SolicitudRepository
    private lateinit var viewModel: SolicitudViewModel
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Usando Mockito porque es más estable para testear Android en local
        repository = mock(SolicitudRepository::class.java)
        mockContext = mock(Context::class.java)

        // Mock default behavior para evitar null pointers con el Vibrator
        whenever(mockContext.getSystemService(Context.VIBRATOR_SERVICE)).thenReturn(null)
        whenever(mockContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)).thenReturn(null)

        viewModel = SolicitudViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `guardarSolicitud con campos vacios actualiza uiState con errores`() {
        // Arrange
        viewModel.onTituloChanged("")
        viewModel.onDescripcionChanged("")

        // Act
        viewModel.guardarSolicitud(mockContext)

        // Assert
        val currentState = viewModel.uiState.value
        assertEquals("El título es obligatorio", currentState.tituloError)
        assertEquals("La descripción del fallo es obligatoria", currentState.descripcionError)
        assertEquals(null, currentState.servicioError) // Servicio ahora es opcional
    }

    @Test
    fun `onTituloChanged actualiza el estado correctamente y limpia el error`() {
        // Act
        viewModel.onTituloChanged("Nuevo Título")

        // Assert
        val state = viewModel.uiState.value
        assertEquals("Nuevo Título", state.titulo)
        assertEquals(null, state.tituloError)
    }
    
    @Test
    fun `setUsuarioSesion actualiza el perfil correctamente`() {
        // Arrange
        val usuarioMock = UsuarioModel(
            username = "jorge",
            contrasena = "123",
            nombre = "Jorge G",
            perfil = "Administrador / Coordinador",
            userId = "ADM-100"
        )
        
        // Act
        viewModel.setUsuarioSesion(usuarioMock)
        
        // Assert
        assertEquals("Administrador / Coordinador", viewModel.perfilUsuario.value)
        assertEquals("jorge", viewModel.usuarioSesion.value?.username)
    }
}