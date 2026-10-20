
package com.example.recordatoriosapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current.applicationContext

    // Repositorio de recordatorios de la Actividad 2
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

    // Repositorio y ViewModel de preferencias
    val preferenciasRepositorio = remember(context) {
        PreferenciasRepository(context)
    }

    val preferenciasModelo: PreferenciasViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return PreferenciasViewModel(
                    preferenciasRepositorio
                ) as T
            }
        }
    )

    val listaEstado by modelo.listaUiState
        .collectAsStateWithLifecycle()

    val formularioEstado by modelo.formularioUiState
        .collectAsStateWithLifecycle()

    val preferencias by preferenciasModelo.preferencias
        .collectAsStateWithLifecycle()

    LaunchedEffect(formularioEstado.guardadoExitoso) {
        if (formularioEstado.guardadoExitoso) {
            navController.popBackStack()
            modelo.reiniciarFormulario()
        }
    }

    // Aplicar preferencias al listado
    val listaFiltrada = remember(listaEstado, preferencias) {
        when (val estado = listaEstado) {
            is ListaUiState.ConDatos -> {
                var datos = estado.recordatorios

                if (!preferencias.mostrarCompletados) {
                    datos = datos.filter {
                        it.estado != EstadoRecordatorio.Completado
                    }
                }

                datos = when (preferencias.ordenamiento) {
                    "titulo" -> datos.sortedBy { it.titulo }

                    "prioridad" -> datos.sortedBy {
                        when (it.prioridad) {
                            Prioridad.Alta -> 0
                            Prioridad.Media -> 1
                            Prioridad.Baja -> 2
                        }
                    }

                    else -> datos.sortedBy { it.id }
                }

                ListaUiState.ConDatos(datos)
            }

            else -> estado
        }
    }

    NavHost(
        navController = navController,
        startDestination = "listado"
    ) {
        composable("listado") {
            PantallaListado(
                estado = listaFiltrada,
                onCrearRecordatorio = {
                    modelo.reiniciarFormulario()
                    navController.navigate("crear")
                },
                onRecordatorioClick = { id ->
                    navController.navigate("detalle/$id")
                },
                onReintentar = {
                    modelo.cargarRecordatorios()
                },
                onPreferenciasClick = {
                    navController.navigate("preferencias")
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

        // Nueva pantalla de preferencias
        composable("preferencias") {
            PantallaPreferencias(
                preferencias = preferencias,
                onTemaOscuroChange =
                    preferenciasModelo::cambiarTemaOscuro,
                onMostrarCompletadosChange =
                    preferenciasModelo::cambiarMostrarCompletados,
                onOrdenamientoChange =
                    preferenciasModelo::cambiarOrdenamiento,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}
