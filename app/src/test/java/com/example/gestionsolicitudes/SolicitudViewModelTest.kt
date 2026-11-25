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
        

        repository = mock(SolicitudRepository::class.java)
        mockContext = mock(Context::class.java)


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

        viewModel.onTituloChanged("")
        viewModel.onDescripcionChanged("")


        viewModel.guardarSolicitud(mockContext)


        val currentState = viewModel.uiState.value
        assertEquals("El título es obligatorio", currentState.tituloError)
        assertEquals("La descripción del fallo es obligatoria", currentState.descripcionError)
        assertEquals(null, currentState.servicioError) // Servicio ahora es opcional
    }

    @Test
    fun `onTituloChanged actualiza el estado correctamente y limpia el error`() {

        viewModel.onTituloChanged("Nuevo Título")


        val state = viewModel.uiState.value
        assertEquals("Nuevo Título", state.titulo)
        assertEquals(null, state.tituloError)
    }
    
    @Test
    fun `setUsuarioSesion actualiza el perfil correctamente`() {

        val usuarioMock = UsuarioModel(
            username = "jorge",
            contrasena = "123",
            nombre = "Jorge G",
            perfil = "Administrador / Coordinador",
            userId = "ADM-100"
        )
        

        viewModel.setUsuarioSesion(usuarioMock)
        

        assertEquals("Administrador / Coordinador", viewModel.perfilUsuario.value)
        assertEquals("jorge", viewModel.usuarioSesion.value?.username)
    }

    @Test
    fun `removerEvidencia quita la uri seleccionada de la lista`() {
        //  agregamos dos evidencias iniciales
        val uri1 = "content://images/foto1.jpg"
        val uri2 = "content://images/foto2.jpg"
        viewModel.agregarEvidencias(listOf(uri1, uri2))

        //removemos la primera
        viewModel.removerEvidencia(uri1)

        //  verificamos que solo quede la segunda
        val evidenciasActuales = viewModel.uiState.value.evidencias
        assertEquals(1, evidenciasActuales.size)
        assertEquals(uri2, evidenciasActuales.first())
    }

    @Test
    fun `resetGuardado limpia la bandera guardadoExitosamente`() {

        // Creamos una entidad simulada real (sin mockk)
        val entidadSimulada = com.example.gestionsolicitudes.data.local.SolicitudEntity(
            titulo = "Test",
            descripcion = "Test",
            categoria = "Web",
            servicio = "Web",
            estado = "Recibida"
        )
        // Simulamos un guardado directo exitoso para levantar la bandera
        viewModel.guardarSolicitudEntity(entidadSimulada)


        viewModel.resetGuardado()


        assertEquals(false, viewModel.uiState.value.guardadoExitosamente)
    }

    @Test
    fun `autenticar usuario valido retorna Exito`() {

        // Usamos el usuario 'cliente' que está hardcodeado en el repositorio
        val resultado = com.example.gestionsolicitudes.data.model.UsuariosRepository.autenticar("cliente", "cliente123")


        assert(resultado is com.example.gestionsolicitudes.data.model.ResultadoAutenticacion.Exito)
    }

    @Test
    fun `autenticar usuario con clave incorrecta retorna Error`() {

        val resultado = com.example.gestionsolicitudes.data.model.UsuariosRepository.autenticar("admin", "clave_falsa")


        assert(resultado is com.example.gestionsolicitudes.data.model.ResultadoAutenticacion.Error)
        val error = resultado as com.example.gestionsolicitudes.data.model.ResultadoAutenticacion.Error
        assertEquals("La contraseña ingresada es incorrecta.", error.mensaje)
    }

    @Test
    fun `autenticar usuario inexistente retorna Error`() {

        val resultado = com.example.gestionsolicitudes.data.model.UsuariosRepository.autenticar("hacker", "12345")


        assert(resultado is com.example.gestionsolicitudes.data.model.ResultadoAutenticacion.Error)
        val error = resultado as com.example.gestionsolicitudes.data.model.ResultadoAutenticacion.Error
        assertEquals("El usuario 'hacker' no existe en el sistema.", error.mensaje)
    }
}