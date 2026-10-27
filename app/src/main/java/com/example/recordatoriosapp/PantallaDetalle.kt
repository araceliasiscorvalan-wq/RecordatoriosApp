
package com.example.recordatoriosapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FondoDetalle = Color(0xFFFAF8FF)
private val VioletaDetalle = Color(0xFF7655C5)
private val TextoDetalle = Color(0xFF29243D)
private val GrisDetalle = Color(0xFF706B80)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    recordatorio: Recordatorio?,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    onEditar: (() -> Unit)? = null,
    onEliminar: (() -> Unit)? = null,
    onCambiarEstado: (() -> Unit)? = null
) {
    var mostrarConfirmacion by remember {
        mutableStateOf(false)
    }

    val colorCategoria = when (recordatorio?.categoria?.nombre) {
        "Estudio" -> Color(0xFFF1E7FF)
        "Trabajo" -> Color(0xFFFFE8DC)
        "Personal" -> Color(0xFFDCF5E8)
        else -> Color(0xFFFFF1C8)
    }

    val emojiCategoria = when (recordatorio?.categoria?.nombre) {
        "Estudio" -> "📚"
        "Trabajo" -> "💼"
        "Personal" -> "🌷"
        else -> "📌"
    }

    Scaffold(
        modifier = modifier,
        containerColor = FondoDetalle,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle de tarea ✨",
                        fontWeight = FontWeight.Bold,
                        color = TextoDetalle
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text(
                            "← Volver",
                            color = VioletaDetalle
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FondoDetalle
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (recordatorio == null) {

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF1E7FF)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔎", fontSize = 44.sp)

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "No se encontró el recordatorio",
                            color = TextoDetalle,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VioletaDetalle
                    )
                ) {
                    Text("Volver al listado")
                }

            } else {

                // Tarjeta principal
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorCategoria
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            emojiCategoria,
                            fontSize = 52.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            recordatorio.titulo,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextoDetalle
                        )

                        Spacer(Modifier.height(12.dp))

                        Surface(
                            color = Color.White.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                recordatorio.categoria.nombre,
                                modifier = Modifier.padding(
                                    horizontal = 18.dp,
                                    vertical = 9.dp
                                ),
                                color = TextoDetalle,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Información del recordatorio
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Text(
                            "Información de la tarea 📝",
                            color = TextoDetalle,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        HorizontalDivider()

                        DetalleCampo(
                            etiqueta = "Descripción",
                            valor = recordatorio.descripcion
                                ?: "Sin descripción"
                        )

                        DetalleCampo(
                            etiqueta = "Prioridad",
                            valor = recordatorio.prioridad.nombre
                        )

                        DetalleCampo(
                            etiqueta = "Estado",
                            valor = recordatorio.estado.nombre
                        )

                        DetalleCampo(
                            etiqueta = "Ubicación",
                            valor = recordatorio.ubicacion
                                ?: "Sin ubicación"
                        )

                        DetalleCampo(
                            etiqueta = "Fecha de aviso",
                            valor = recordatorio.fechaAviso
                                ?: "Sin fecha de aviso"
                        )

                        DetalleCampo(
                            etiqueta = "Identificador",
                            valor = recordatorio.id.toString()
                        )
                    }
                }

                // Las acciones aparecen cuando se conectan
                // con el ViewModel y Room.
                if (onCambiarEstado != null) {
                    Button(
                        onClick = onCambiarEstado,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF258C66)
                        )
                    ) {
                        Text(
                            if (
                                recordatorio.estado ==
                                EstadoRecordatorio.Completado
                            ) {
                                "↩ Marcar como pendiente"
                            } else {
                                "✓ Marcar como completada"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (onEditar != null) {
                    Button(
                        onClick = onEditar,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VioletaDetalle
                        )
                    ) {
                        Text(
                            "✏ Editar recordatorio",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (onEliminar != null) {
                    OutlinedButton(
                        onClick = {
                            mostrarConfirmacion = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFC13D56)
                        )
                    ) {
                        Text(
                            "🗑 Eliminar recordatorio",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                TextButton(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Volver a mis tareas",
                        color = VioletaDetalle
                    )
                }
            }
        }
    }

    if (mostrarConfirmacion && onEliminar != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacion = false
            },
            title = {
                Text("¿Eliminar recordatorio?")
            },
            text = {
                Text(
                    "Esta acción eliminará la tarea. " +
                            "¿Querés continuar?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                        onEliminar()
                    }
                ) {
                    Text(
                        "Eliminar",
                        color = Color(0xFFC13D56)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DetalleCampo(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = etiqueta,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = GrisDetalle
        )

        Text(
            text = valor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextoDetalle
        )
    }
}
