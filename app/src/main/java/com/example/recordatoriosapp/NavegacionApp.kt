
package com.example.recordatoriosapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavegacionApp() {

    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext

    // Base de datos Room
    val baseDatos = remember(context) {
        RecordatoriosDatabase.obtenerInstancia(context)
    }

    val dao = remember(baseDatos) {
        baseDatos.recordatorioDao()
    }

    // Repositorio local
    val repositorio = remember(dao) {
        RecordatoriosRepositoryRoom(dao)
    }

    // Sincronizador con nuestra API
    val sincronizador = remember(dao) {
        SincronizadorRecordatorios(
            dao = dao,
            api = RetrofitClient.apiService
        )
    }

    // ViewModel principal
    val modelo: RecordatoriosViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return RecordatoriosViewModel(
                    repositorio = repositorio,
                    sincronizador = sincronizador
                ) as T
            }
        }
    )

    // Preferencias guardadas con DataStore
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

    val sincronizando by modelo.sincronizando
        .collectAsStateWithLifecycle()

    val mensajeSincronizacion by modelo.mensajeSincronizacion
        .collectAsStateWithLifecycle()

    val preferencias by preferenciasModelo.preferencias
        .collectAsStateWithLifecycle()

    // Regresar después de crear o editar correctamente
    LaunchedEffect(formularioEstado.guardadoExitoso) {
        if (formularioEstado.guardadoExitoso) {
            navController.popBackStack()
            modelo.reiniciarFormulario()
        }
    }

    // Aplicar las preferencias al listado
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
                    "titulo" -> datos.sortedBy {
                        it.titulo
                    }

                    "prioridad" -> datos.sortedBy {
                        when (it.prioridad) {
                            Prioridad.Alta -> 0
                            Prioridad.Media -> 1
                            Prioridad.Baja -> 2
                        }
                    }

                    else -> datos.sortedBy {
                        it.id
                    }
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

        // PANTALLA PRINCIPAL
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
                },
                sincronizando = sincronizando,
                mensajeSincronizacion = mensajeSincronizacion,
                onSincronizar = {
                    modelo.sincronizarRecordatorios()
                },
                onLimpiarMensajeSincronizacion = {
                    modelo.limpiarMensajeSincronizacion()
                }
            )
        }

        // CREAR RECORDATORIO
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

        // DETALLE DEL RECORDATORIO
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

            LaunchedEffect(id, listaEstado) {
                val estado = listaEstado

                if (estado is ListaUiState.ConDatos) {
                    val actualizado = estado.recordatorios.find {
                        it.id == id
                    }

                    if (actualizado != null) {
                        recordatorio = actualizado
                    }
                }
            }

            PantallaDetalle(
                recordatorio = recordatorio,
                onVolver = {
                    navController.popBackStack()
                },
                onCambiarEstado = {
                    modelo.cambiarEstadoRecordatorio(id) { actualizado ->
                        if (actualizado != null) {
                            recordatorio = actualizado
                        }
                    }
                },
                onEliminar = {
                    modelo.eliminarRecordatorio(id) { eliminado ->
                        if (eliminado) {
                            navController.popBackStack()
                        }
                    }
                },
                onEditar = {
                    navController.navigate("editar/$id")
                }
            )
        }

        // EDITAR RECORDATORIO
        composable(
            route = "editar/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { entrada ->

            val id = entrada.arguments?.getInt("id") ?: -1

            LaunchedEffect(id) {
                modelo.cargarRecordatorioParaEditar(id)
            }

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
                },
                modoEdicion = true
            )
        }

        // PREFERENCIAS
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
