package com.example.gestionsolicitudes

import android.content.Context
import com.example.gestionsolicitudes.data.repository.SolicitudRepository
import com.example.gestionsolicitudes.ui.solicitud.SolicitudViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SolicitudViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SolicitudRepository
    private lateinit var viewModel: SolicitudViewModel
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)

        // Mock default behavior para evitar null pointers con el Vibrator
        every { mockContext.getSystemService(Context.VIBRATOR_SERVICE) } returns null
        every { mockContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) } returns null

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
        assertEquals("Seleccione una categoría", currentState.categoriaError)
        assertEquals("Seleccione un servicio", currentState.servicioError)
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
}