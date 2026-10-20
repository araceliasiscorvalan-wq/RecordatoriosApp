
package com.example.recordatoriosapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    val repositorio = remember {
        RecordatoriosRepositoryMemoria()
    }

    val modelo: RecordatoriosViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return RecordatoriosViewModel(repositorio) as T
            }
        }
    )

    val listaEstado by modelo.listaUiState
        .collectAsStateWithLifecycle()

    val formularioEstado by modelo.formularioUiState
        .collectAsStateWithLifecycle()

    LaunchedEffect(formularioEstado.guardadoExitoso) {
        if (formularioEstado.guardadoExitoso) {
            navController.popBackStack()
            modelo.reiniciarFormulario()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "listado"
    ) {

        composable("listado") {
            PantallaListado(
                estado = listaEstado,
                onCrearRecordatorio = {
                    modelo.reiniciarFormulario()
                    navController.navigate("crear")
                },
                onRecordatorioClick = { id ->
                    navController.navigate("detalle/$id")
                },
                onReintentar = {
                    modelo.cargarRecordatorios()
                }
            )
        }

        composable("crear") {
            PantallaCreacion(
                estado = formularioEstado,
                onTituloChange = modelo::actualizarTitulo,
                onDescripcionChange = modelo::actualizarDescripcion,
                onCategoriaChange = modelo::actualizarCategoria,
                onPrioridadChange = modelo::actualizarPrioridad,
                onGuardar = modelo::guardarRecordatorio,
                onCancelar = {
                    modelo.reiniciarFormulario()
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "detalle/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { entrada ->
            val id = entrada.arguments?.getInt("id") ?: -1

            var recordatorio by remember(id) {
                mutableStateOf<Recordatorio?>(null)
            }

            LaunchedEffect(id) {
                modelo.obtenerRecordatorio(id) {
                    recordatorio = it
                }
            }

            PantallaDetalle(
                recordatorio = recordatorio,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}

