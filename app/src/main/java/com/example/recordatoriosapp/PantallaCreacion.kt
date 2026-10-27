
package com.example.recordatoriosapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FondoFormulario = Color(0xFFFAF8FF)
private val VioletaFormulario = Color(0xFF7655C5)
private val TextoFormulario = Color(0xFF29243D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCreacion(
    estado: FormularioUiState,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onCategoriaChange: (String) -> Unit,
    onPrioridadChange: (Prioridad) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
    modoEdicion: Boolean = false
) {
    val formularioValido =
        estado.titulo.trim().length >= 4 &&
                estado.descripcion.length <= 200 &&
                estado.categoria in DatosSimulados.categorias &&
                estado.prioridad != null &&
                !estado.guardando

    Scaffold(
        modifier = modifier,
        containerColor = FondoFormulario,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (modoEdicion) {
                            "Editar tarea ✏️"
                        } else {
                            "Nueva tarea 🌈"
                        },
                        fontWeight = FontWeight.Bold,
                        color = TextoFormulario
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onCancelar) {
                        Text(
                            "← Volver",
                            color = VioletaFormulario
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FondoFormulario
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF1E7FF)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = if (modoEdicion) {
                            "Dale un nuevo toque a tu tarea ✨"
                        } else {
                            "¡Organizá tus ideas! ✨"
                        },
                        color = TextoFormulario,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Completá los datos de tu recordatorio",
                        color = Color(0xFF706B80),
                        fontSize = 14.sp
                    )
                }
            }

            OutlinedTextField(
                value = estado.titulo,
                onValueChange = onTituloChange,
                label = { Text("Título") },
                singleLine = true,
                isError = estado.errorTitulo != null,
                supportingText = {
                    Text(
                        estado.errorTitulo
                            ?: "Mínimo 4 caracteres"
                    )
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = onDescripcionChange,
                label = { Text("Descripción") },
                minLines = 3,
                maxLines = 5,
                isError = estado.errorDescripcion != null ||
                        estado.descripcion.length > 200,
                supportingText = {
                    Text(
                        estado.errorDescripcion
                            ?: "${estado.descripcion.length}/200 caracteres"
                    )
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Elegí una categoría 🎨",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextoFormulario
            )

            listOf(
                Triple("Estudio", "📚", Color(0xFFF1E7FF)),
                Triple("Trabajo", "💼", Color(0xFFFFE8DC)),
                Triple("Personal", "🌷", Color(0xFFDCF5E8))
            ).forEach { (categoria, emoji, color) ->

                val seleccionada = estado.categoria == categoria

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCategoriaChange(categoria)
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = color
                    ),
                    border = if (seleccionada) {
                        BorderStroke(2.dp, VioletaFormulario)
                    } else {
                        null
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emoji, fontSize = 24.sp)

                        Spacer(Modifier.width(12.dp))

                        Text(
                            categoria,
                            modifier = Modifier.weight(1f),
                            color = TextoFormulario,
                            fontWeight = FontWeight.Medium
                        )

                        RadioButton(
                            selected = seleccionada,
                            onClick = {
                                onCategoriaChange(categoria)
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = VioletaFormulario
                            )
                        )
                    }
                }
            }

            estado.errorCategoria?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Text(
                text = "¿Qué prioridad tiene? ⭐",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextoFormulario
            )

            listOf(
                Prioridad.Alta,
                Prioridad.Media,
                Prioridad.Baja
            ).forEach { prioridad ->

                val color = when (prioridad) {
                    Prioridad.Alta -> Color(0xFFFFE2E7)
                    Prioridad.Media -> Color(0xFFFFF1C8)
                    Prioridad.Baja -> Color(0xFFDCF5E8)
                }

                val seleccionada = estado.prioridad == prioridad

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onPrioridadChange(prioridad)
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = color
                    ),
                    border = if (seleccionada) {
                        BorderStroke(2.dp, VioletaFormulario)
                    } else {
                        null
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (prioridad) {
                                Prioridad.Alta -> "🔴"
                                Prioridad.Media -> "🟡"
                                Prioridad.Baja -> "🟢"
                            },
                            fontSize = 20.sp
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            prioridad.nombre,
                            modifier = Modifier.weight(1f),
                            color = TextoFormulario,
                            fontWeight = FontWeight.Medium
                        )

                        RadioButton(
                            selected = seleccionada,
                            onClick = {
                                onPrioridadChange(prioridad)
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = VioletaFormulario
                            )
                        )
                    }
                }
            }

            estado.errorPrioridad?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            estado.errorGuardado?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (estado.guardando) {
                CircularProgressIndicator(
                    color = VioletaFormulario
                )
                Text("Guardando recordatorio...")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    enabled = !estado.guardando,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = onGuardar,
                    enabled = formularioValido,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VioletaFormulario
                    )
                ) {
                    Text(
                        if (modoEdicion) {
                            "Guardar cambios"
                        } else {
                            "Crear tarea"
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
