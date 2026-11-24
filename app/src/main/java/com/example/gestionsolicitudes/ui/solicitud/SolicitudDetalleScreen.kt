package com.example.gestionsolicitudes.ui.solicitud

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.gestionsolicitudes.data.local.SolicitudEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitudId: Long,
    viewModel: SolicitudViewModel,
    onEditarClick: (Long) -> Unit = {},
    onNavigateBack: () -> Unit
) {
    val lista by viewModel.listaSolicitudes.collectAsState()
    val perfil by viewModel.perfilUsuario.collectAsState()
    
    val solicitud: SolicitudEntity? = remember(lista, solicitudId) {
        lista.find { it.id == solicitudId }
    }
    val context = LocalContext.current

    // REGLA: El cliente no puede editar si el estado ya no es "Recibida" o "Pendiente de información"
    val puedeEditar = remember(perfil, solicitud) {
        if (solicitud == null) false
        else if (perfil == "Cliente") {
            solicitud.estado.equals("Recibida", ignoreCase = true) || 
            solicitud.estado.equals("Pendiente de información", ignoreCase = true)
        } else {
            true // Admin y Técnico siempre pueden editar para avanzar estados
        }
    }

    val listaEvidencias = remember(solicitud?.evidenciaUri) {
        solicitud?.evidenciaUri?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    var imagenParaFullscreen by remember { mutableStateOf<String?>(null) }
    val formateador = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) }

    if (imagenParaFullscreen != null) {
        Dialog(
            onDismissRequest = { imagenParaFullscreen = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .clickable { imagenParaFullscreen = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imagenParaFullscreen,
                    contentDescription = "Evidencia Ampliada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Reclamo #${solicitudId}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                },
                actions = {
                    if (solicitud != null && puedeEditar) {
                        IconButton(onClick = { onEditarClick(solicitud.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar Reclamo"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (solicitud == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No se encontró la información del reclamo.", color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Código: ${solicitud.codigoTracking}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "ID Interno: #${solicitud.id}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = solicitud.estado,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Título del Reclamo", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text(solicitud.titulo, style = MaterialTheme.typography.titleLarge)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Text("Descripción del Problema", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text(solicitud.descripcion, style = MaterialTheme.typography.bodyLarge)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Categoría", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(solicitud.categoria, style = MaterialTheme.typography.bodyMedium)
                            }
                            if (solicitud.servicio.isNotBlank()) {
                                Column {
                                    Text("Modalidad / Servicio", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                    Text(solicitud.servicio, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Text("Fecha y Hora de Ingreso", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text(formateador.format(Date(solicitud.fechaHora)), style = MaterialTheme.typography.bodySmall)

                        // Mostrar la resolución solo a Técnicos/Admin, o al Cliente si la solicitud está Resuelta/Cerrada
                        if (!solicitud.comentarioTecnico.isNullOrBlank()) {
                            val mostrarAlCliente = solicitud.estado.equals("Resuelta", ignoreCase = true) || 
                                                   solicitud.estado.equals("Cerrada", ignoreCase = true)
                                                   
                            if (perfil != "Cliente" || mostrarAlCliente) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Text("Resolución / Avances (Área Técnica)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Text(solicitud.comentarioTecnico, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }

                // Botones de acción principales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (puedeEditar) {
                        Button(
                            onClick = { onEditarClick(solicitud.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar")
                        }
                    }
                    
                    // Simulación Compartir por WhatsApp (Cualquier perfil puede compartir, pero enfocado al cliente)
                    OutlinedButton(
                        onClick = {
                            val mensaje = "Hola! Mi solicitud *'${solicitud.titulo}'* tiene el código de seguimiento *${solicitud.codigoTracking}*. Su estado actual es: *${solicitud.estado}*."
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, mensaje)
                                // setPackage("com.whatsapp") // En producción descomentar para forzar WhatsApp
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Compartir estado vía..."))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WhatsApp")
                    }
                }

                if (!puedeEditar && perfil == "Cliente") {
                    if (solicitud.estado.equals("Resuelta", ignoreCase = true)) {
                        // Acciones para el Cliente cuando el técnico resuelve el requerimiento
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Atención Requerida",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "El área técnica ha marcado esta solicitud como resuelta. Por favor, lee la resolución entregada arriba y confirma si estás de acuerdo, o solicita su reapertura si el problema persiste.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.actualizarSolicitud(solicitud.copy(estado = "En revisión", fechaModificacion = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())))
                                            onNavigateBack()
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reabrir")
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.actualizarSolicitud(solicitud.copy(estado = "Cerrada", fechaModificacion = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())))
                                            onNavigateBack()
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Confirmar")
                                    }
                                }
                            }
                        }
                    } else if (solicitud.estado.equals("Cerrada", ignoreCase = true)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(
                                text = "Esta solicitud fue cerrada exitosamente.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        // Mensaje de que no puede editar (cuando está asignada o en proceso)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(
                                text = "Esta solicitud ya fue gestionada ('${solicitud.estado}'). Ya no es posible editar sus datos ni añadir nueva evidencia, a menos que un administrador solicite más información.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Archivos de Evidencia (${listaEvidencias.size})",
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (listaEvidencias.isEmpty()) {
                            Text(
                                text = "No se adjuntaron fotografías ni evidencias para este reclamo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        } else {
                            Text(
                                text = "Toca cualquier miniatura para verla en pantalla completa o abrirla externamente:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(listaEvidencias) { uriStr ->
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.LightGray)
                                            .clickable { imagenParaFullscreen = uriStr }
                                    ) {
                                        AsyncImage(
                                            model = uriStr,
                                            contentDescription = "Evidencia",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    try {
                                        val primeraUri = Uri.parse(listaEvidencias.first())
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(primeraUri, "image/*")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Abrir evidencia con..."))
                                    } catch (_: Exception) {
                                        imagenParaFullscreen = listaEvidencias.first()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Abrir en visor del sistema")
                            }
                        }
                    }
                }
            }
        }
    }
}
