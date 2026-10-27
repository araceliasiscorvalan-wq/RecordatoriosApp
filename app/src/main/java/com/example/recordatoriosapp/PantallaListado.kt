
package com.example.recordatoriosapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Paleta de colores de nuestra aplicación
private val FondoApp = Color(0xFFFAF8FF)
private val Violeta = Color(0xFF7655C5)
private val TextoOscuro = Color(0xFF29243D)
private val TextoSuave = Color(0xFF706B80)
private val Lavanda = Color(0xFFF1E7FF)
private val Durazno = Color(0xFFFFE8DC)
private val Menta = Color(0xFFDCF5E8)
private val Amarillo = Color(0xFFFFF1C8)

@Composable
fun PantallaListado(
    estado: ListaUiState,
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (Int) -> Unit,
    onReintentar: () -> Unit,
    onPreferenciasClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = FondoApp,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCrearRecordatorio,
                containerColor = Violeta,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                text = {
                    Text(
                        "Nueva tarea",
                        fontWeight = FontWeight.Bold
                    )
                },
                icon = {
                    Text(
                        "+",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Encabezado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 24.dp,
                        bottom = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "¡Hola! 👋",
                        color = TextoSuave,
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Mis recordatorios",
                        color = TextoOscuro,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Organizá tu día a tu manera ✨",
                        color = TextoSuave,
                        fontSize = 13.sp
                    )
                }

                FilledTonalIconButton(
                    onClick = onPreferenciasClick,
                    colors = IconButtonDefaults
                        .filledTonalIconButtonColors(
                            containerColor = Lavanda,
                            contentColor = Violeta
                        )
                ) {
                    Text(
                        text = "⚙",
                        fontSize = 22.sp
                    )
                }
            }

            when (estado) {
                ListaUiState.Cargando -> {
                    EstadoMensaje(
                        mensaje = "Cargando tus tareas... ✨"
                    )
                }

                ListaUiState.Vacio -> {
                    EstadoMensaje(
                        mensaje = "🌈\n\n¡Un día lleno de posibilidades!\nTodavía no tenés recordatorios.",
                        textoBoton = "Crear mi primera tarea",
                        onAccion = onCrearRecordatorio
                    )
                }

                is ListaUiState.Error -> {
                    EstadoMensaje(
                        mensaje = estado.mensaje,
                        textoBoton = "Volver a intentar",
                        onAccion = onReintentar
                    )
                }

                is ListaUiState.ConDatos -> {
                    val total = estado.recordatorios.size

                    val pendientes = estado.recordatorios.count {
                        it.estado == EstadoRecordatorio.Pendiente
                    }

                    val completadas = total - pendientes

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        item {
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Lavanda
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = "Tu progreso de hoy 🌟",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoOscuro
                                    )

                                    Spacer(
                                        modifier = Modifier.height(12.dp)
                                    )

                                    Row(
                                        horizontalArrangement =
                                            Arrangement.spacedBy(12.dp)
                                    ) {
                                        IndicadorResumen(
                                            numero = total,
                                            etiqueta = "Total",
                                            modifier = Modifier.weight(1f)
                                        )

                                        IndicadorResumen(
                                            numero = pendientes,
                                            etiqueta = "Pendientes",
                                            modifier = Modifier.weight(1f)
                                        )

                                        IndicadorResumen(
                                            numero = completadas,
                                            etiqueta = "Hechas",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier.height(16.dp)
                                    )

                                    LinearProgressIndicator(
                                        progress = {
                                            if (total == 0) 0f
                                            else completadas.toFloat() / total
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp),
                                        color = Violeta,
                                        trackColor = Color.White
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Tus tareas 📝",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro,
                                modifier = Modifier.padding(
                                    top = 10.dp,
                                    bottom = 4.dp
                                )
                            )
                        }

                        items(
                            items = estado.recordatorios,
                            key = { it.id }
                        ) { recordatorio ->
                            TarjetaRecordatorioItem(
                                recordatorio = recordatorio,
                                onClick = {
                                    onRecordatorioClick(
                                        recordatorio.id
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicadorResumen(
    numero: Int,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                Color.White.copy(alpha = 0.8f),
                RoundedCornerShape(16.dp)
            )
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = numero.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Violeta
        )

        Text(
            text = etiqueta,
            fontSize = 11.sp,
            color = TextoSuave
        )
    }
}

@Composable
fun TarjetaRecordatorioItem(
    recordatorio: Recordatorio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorTarjeta = when (
        recordatorio.categoria.nombre
    ) {
        "Estudio" -> Lavanda
        "Trabajo" -> Durazno
        "Personal" -> Menta
        else -> Amarillo
    }

    val colorPrioridad = when (recordatorio.prioridad) {
        Prioridad.Alta -> Color(0xFFD74F65)
        Prioridad.Media -> Color(0xFFB77818)
        Prioridad.Baja -> Color(0xFF21845B)
    }

    val completado =
        recordatorio.estado == EstadoRecordatorio.Completado

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorTarjeta
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (
                        recordatorio.categoria.nombre
                    ) {
                        "Estudio" -> "📚"
                        "Trabajo" -> "💼"
                        "Personal" -> "🌷"
                        else -> "📌"
                    },
                    fontSize = 27.sp
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = recordatorio.titulo,
                        color = TextoOscuro,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = recordatorio.categoria.nombre,
                        color = TextoSuave,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "›",
                    color = TextoOscuro,
                    fontSize = 30.sp
                )
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.8f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colorPrioridad.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "● ${recordatorio.prioridad.nombre}",
                        modifier = Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 7.dp
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorPrioridad
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = if (completado) {
                            "✓ Completado"
                        } else {
                            "◷ Pendiente"
                        },
                        modifier = Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 7.dp
                        ),
                        fontSize = 12.sp,
                        color = TextoOscuro
                    )
                }
            }
        }
    }
}

@Composable
fun EstadoMensaje(
    mensaje: String,
    modifier: Modifier = Modifier,
    textoBoton: String? = null,
    onAccion: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = mensaje,
            fontSize = 18.sp,
            color = TextoOscuro,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (textoBoton != null) {
            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = onAccion,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Violeta
                )
            ) {
                Text(
                    text = textoBoton,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    )
                )
            }
        }
    }
}
