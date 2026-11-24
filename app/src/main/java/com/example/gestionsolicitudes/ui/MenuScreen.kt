package com.example.gestionsolicitudes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestionsolicitudes.ui.solicitud.SolicitudViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: SolicitudViewModel,
    onNavigateToSolicitudes: () -> Unit,
    onLogout: (() -> Unit)? = null
) {
    val perfil by viewModel.perfilUsuario.collectAsState()
    val usuarioSesion by viewModel.usuarioSesion.collectAsState()
    
    var mostrarDialogoLogout by remember { mutableStateOf(false) }

    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión y volver a la pantalla de inicio?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoLogout = false
                        onLogout?.invoke()
                    }
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Principal - $perfil") },
                navigationIcon = {
                    if (onLogout != null) {
                        IconButton(onClick = { mostrarDialogoLogout = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Cerrar sesión / Regresar"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Sesión Activa Autenticada", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = usuarioSesion?.nombre ?: "Usuario Desconocido",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Usuario: ${usuarioSesion?.username ?: "-"} | ID: ${usuarioSesion?.userId ?: "-"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Perfil / Rol: $perfil",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = when (perfil) {
                    "Cliente" -> "Bienvenido. Consulta el estado de tus solicitudes (En revisión, En proceso, Resueltas) y registra nuevos requerimientos."
                    "Administrador / Coordinador" -> "Panel de Control Global. Supervisa todas las solicitudes del sistema, asigna técnicos, clasifica requerimientos y revisa métricas."
                    else -> "Panel Técnico. Revisa las solicitudes asignadas a tu área técnica, registra avances y actualiza estados de resolución."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigateToSolicitudes,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (perfil) {
                        "Cliente" -> "Mis Solicitudes y Seguimiento"
                        "Administrador / Coordinador" -> "Panel Administrativo de Solicitudes"
                        else -> "Solicitudes Asignadas a Técnico"
                    }
                )
            }
        }
    }
}
