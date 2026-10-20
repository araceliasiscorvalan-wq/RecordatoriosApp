
package com.example.recordatoriosapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    recordatorio: Recordatorio?,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detalle del recordatorio") },
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (recordatorio == null) {
                Text(
                    text = "No se encontró el recordatorio",
                    style = MaterialTheme.typography.titleMedium
                )

                Button(onClick = onVolver) {
                    Text("Volver al listado")
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = recordatorio.titulo,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        HorizontalDivider()

                        DetalleCampo(
                            etiqueta = "Identificador",
                            valor = recordatorio.id.toString()
                        )

                        DetalleCampo(
                            etiqueta = "Descripción",
                            valor = recordatorio.descripcion
                                ?: "Sin descripción"
                        )

                        DetalleCampo(
                            etiqueta = "Categoría",
                            valor = recordatorio.categoria.nombre
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
                    }
                }

                Button(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al listado")
                }
            }
        }
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
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

