
package com.example.recordatoriosapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

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
    modifier: Modifier = Modifier
) {
    val formularioValido =
        estado.titulo.trim().length >= 4 &&
                estado.descripcion.length <= 200 &&
                estado.categoria in DatosSimulados.categorias &&
                estado.prioridad != null &&
                !estado.guardando

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Nuevo recordatorio") })
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

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
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        estado.errorTitulo?.let { error(it) }
                    }
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
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Categoría",
                style = MaterialTheme.typography.titleMedium
            )

            DatosSimulados.categorias.sorted().forEach { categoria ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = estado.categoria == categoria,
                        onClick = { onCategoriaChange(categoria) }
                    )
                    Text(
                        text = categoria,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            estado.errorCategoria?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Text(
                text = "Prioridad",
                style = MaterialTheme.typography.titleMedium
            )

            listOf(
                Prioridad.Alta,
                Prioridad.Media,
                Prioridad.Baja
            ).forEach { prioridad ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = estado.prioridad == prioridad,
                        onClick = { onPrioridadChange(prioridad) }
                    )
                    Text(
                        text = prioridad.nombre,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            estado.errorPrioridad?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            estado.errorGuardado?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (estado.guardando) {
                CircularProgressIndicator()
                Text("Guardando recordatorio...")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    enabled = !estado.guardando,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = onGuardar,
                    enabled = formularioValido,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
