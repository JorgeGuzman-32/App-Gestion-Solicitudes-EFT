package com.example.gestionsolicitudes.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.gestionsolicitudes.data.model.ResultadoAutenticacion
import com.example.gestionsolicitudes.data.model.UsuariosRepository
import com.example.gestionsolicitudes.ui.solicitud.SolicitudViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: SolicitudViewModel,
    onLoginSuccess: () -> Unit
) {
    var usuario by remember { mutableStateOf("cliente") }
    var contrasena by remember { mutableStateOf("cliente123") }

    var usuarioError by remember { mutableStateOf<String?>(null) }
    var contrasenaError by remember { mutableStateOf<String?>(null) }
    var loginGeneralError by remember { mutableStateOf<String?>(null) }

    // Función auxiliar para auto-completar según el botón presionado
    fun autocompletarPorPerfil(nuevoUsuario: String, nuevaContrasena: String) {
        usuario = nuevoUsuario
        contrasena = nuevaContrasena
        loginGeneralError = null
        usuarioError = null
        contrasenaError = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Gestión Móvil y Seguimiento",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "de Solicitudes de Clientes",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "PostVenta, Garantías y Reclamos",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = {
                usuario = it
                usuarioError = null
                loginGeneralError = null
            },
            label = { Text("Usuario") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            isError = usuarioError != null || loginGeneralError != null,
            supportingText = {
                AnimatedVisibility(visible = usuarioError != null, enter = fadeIn() + expandVertically()) {
                    Text(text = usuarioError ?: "", color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                contrasenaError = null
                loginGeneralError = null
            },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = contrasenaError != null || loginGeneralError != null,
            supportingText = {
                AnimatedVisibility(visible = contrasenaError != null, enter = fadeIn() + expandVertically()) {
                    Text(text = contrasenaError ?: "", color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(
            visible = loginGeneralError != null,
            enter = fadeIn() + expandVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = loginGeneralError ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val uEmpty = if (usuario.isBlank()) "El usuario es obligatorio" else null
                val cEmpty = if (contrasena.isBlank()) "La contraseña es obligatoria" else null

                if (uEmpty != null || cEmpty != null) {
                    usuarioError = uEmpty
                    contrasenaError = cEmpty
                    return@Button
                }

                // Autenticar credenciales
                when (val resultado = UsuariosRepository.autenticar(usuario, contrasena)) {
                    is ResultadoAutenticacion.Exito -> {
                        viewModel.setUsuarioSesion(resultado.usuario)
                        onLoginSuccess()
                    }
                    is ResultadoAutenticacion.Error -> {
                        loginGeneralError = resultado.mensaje
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta con credenciales de prueba por perfil
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🔑 Credenciales de Prueba por Rol:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AssistChip(
                        onClick = { autocompletarPorPerfil("cliente", "cliente123") },
                        label = { Text("Cliente: cliente / cliente123") }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AssistChip(
                        onClick = { autocompletarPorPerfil("admin", "admin123") },
                        label = { Text("Admin: admin / admin123") }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AssistChip(
                        onClick = { autocompletarPorPerfil("tecnico1", "tecnico123") },
                        label = { Text("Francisco: tecnico / tecnico123") }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AssistChip(
                        onClick = { autocompletarPorPerfil("tecnico2", "tecnico123") },
                        label = { Text("Pedro: tecnico / tecnico123") }
                    )
                }
            }
        }
    }
}
