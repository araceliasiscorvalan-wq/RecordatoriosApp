
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
fun PantallaPreferencias(
    preferencias: PreferenciasApp,
    onTemaOscuroChange: (Boolean) -> Unit,
    onMostrarCompletadosChange: (Boolean) -> Unit,
    onOrdenamientoChange: (String) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Preferencias") },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Configuración de la aplicación",
                style = MaterialTheme.typography.titleLarge
            )

            HorizontalDivider()

            // Preferencia 1: tema oscuro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tema oscuro")
                Switch(
                    checked = preferencias.temaOscuro,
                    onCheckedChange = onTemaOscuroChange
                )
            }

            // Preferencia 2: mostrar tareas completadas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mostrar completados")
                Switch(
                    checked = preferencias.mostrarCompletados,
                    onCheckedChange = onMostrarCompletadosChange
                )
            }

            HorizontalDivider()

            // Preferencia 3: ordenamiento
            Text(
                text = "Ordenar recordatorios por:",
                style = MaterialTheme.typography.titleMedium
            )

            listOf(
                "fecha" to "Fecha",
                "titulo" to "Título",
                "prioridad" to "Prioridad"
            ).forEach { (valor, etiqueta) ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = preferencias.ordenamiento == valor,
                        onClick = { onOrdenamientoChange(valor) }
                    )

                    Text(
                        text = etiqueta,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar y volver")
            }
        }
    }
}
