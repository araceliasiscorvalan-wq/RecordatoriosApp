
package com.example.recordatoriosapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(
                title = {
                    Text("Mis recordatorios")
                },
                actions = {
                    TextButton(
                        onClick = onPreferenciasClick
                    ) {
                        Text("⚙️ Ajustes")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCrearRecordatorio
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (estado) {
                ListaUiState.Cargando -> {
                    EstadoMensaje(
                        mensaje = "Cargando recordatorios..."
                    )
                }

                ListaUiState.Vacio -> {
                    EstadoMensaje(
                        mensaje = "Todavía no hay recordatorios",
                        textoBoton = "Crear recordatorio",
                        onAccion = onCrearRecordatorio
                    )
                }

                is ListaUiState.Error -> {
                    EstadoMensaje(
                        mensaje = estado.mensaje,
                        textoBoton = "Reintentar",
                        onAccion = onReintentar
                    )
                }

                is ListaUiState.ConDatos -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
fun TarjetaRecordatorioItem(
    recordatorio: Recordatorio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = recordatorio.titulo,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Categoría: ${recordatorio.categoria.nombre}"
            )

            Text(
                text = "Prioridad: ${recordatorio.prioridad.nombre}"
            )

            Text(
                text = "Estado: ${recordatorio.estado.nombre}"
            )
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
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge
        )

        if (textoBoton != null) {
            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = onAccion
            ) {
                Text(textoBoton)
            }
        }
    }
}
