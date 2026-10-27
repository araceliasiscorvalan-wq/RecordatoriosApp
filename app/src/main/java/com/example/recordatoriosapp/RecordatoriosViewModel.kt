
package com.example.recordatoriosapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecordatoriosViewModel(
    private val repositorio: RecordatoriosRepository
) : ViewModel() {

    private val _listaUiState =
        MutableStateFlow<ListaUiState>(ListaUiState.Cargando)

    val listaUiState: StateFlow<ListaUiState> =
        _listaUiState.asStateFlow()

    private val _formularioUiState =
        MutableStateFlow(FormularioUiState())

    val formularioUiState: StateFlow<FormularioUiState> =
        _formularioUiState.asStateFlow()

    private var trabajoListado: Job? = null

    // ID del recordatorio que estamos editando.
    // null significa que estamos creando uno nuevo.
    private var idEnEdicion: Int? = null

    init {
        cargarRecordatorios()
    }

    fun cargarRecordatorios() {
        trabajoListado?.cancel()

        trabajoListado = viewModelScope.launch {
            _listaUiState.value = ListaUiState.Cargando

            try {
                if (repositorio is RecordatoriosRepositoryRoom) {
                    repositorio.recordatoriosFlow.collectLatest { datos ->
                        _listaUiState.value = if (datos.isEmpty()) {
                            ListaUiState.Vacio
                        } else {
                            ListaUiState.ConDatos(datos)
                        }
                    }
                } else {
                    val datos = repositorio.obtenerRecordatorios()

                    _listaUiState.value = if (datos.isEmpty()) {
                        ListaUiState.Vacio
                    } else {
                        ListaUiState.ConDatos(datos)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _listaUiState.value = ListaUiState.Error(
                    "No se pudieron cargar los recordatorios"
                )
            }
        }
    }

    fun actualizarTitulo(valor: String) {
        _formularioUiState.update {
            it.copy(titulo = valor, errorTitulo = null)
        }
    }

    fun actualizarDescripcion(valor: String) {
        _formularioUiState.update {
            it.copy(
                descripcion = valor,
                errorDescripcion = null
            )
        }
    }

    fun actualizarCategoria(valor: String) {
        _formularioUiState.update {
            it.copy(categoria = valor, errorCategoria = null)
        }
    }

    fun actualizarPrioridad(valor: Prioridad) {
        _formularioUiState.update {
            it.copy(prioridad = valor, errorPrioridad = null)
        }
    }

    private fun erroresFormulario(
        estado: FormularioUiState
    ): FormularioUiState {
        return estado.copy(
            errorTitulo = if (estado.titulo.trim().length < 4) {
                "El título debe tener al menos 4 caracteres"
            } else null,

            errorDescripcion = if (estado.descripcion.length > 200) {
                "La descripción no puede superar 200 caracteres"
            } else null,

            errorCategoria = if (
                estado.categoria !in DatosSimulados.categorias
            ) {
                "Seleccioná una categoría válida"
            } else null,

            errorPrioridad = if (estado.prioridad == null) {
                "Seleccioná una prioridad"
            } else null
        )
    }

    fun formularioValido(): Boolean {
        val estado = erroresFormulario(_formularioUiState.value)

        return estado.errorTitulo == null &&
                estado.errorDescripcion == null &&
                estado.errorCategoria == null &&
                estado.errorPrioridad == null &&
                !estado.guardando
    }

    // Cargar los datos originales para editar
    fun cargarRecordatorioParaEditar(id: Int) {
        idEnEdicion = id
        _formularioUiState.value = FormularioUiState(
            guardando = true
        )

        viewModelScope.launch {
            try {
                val recordatorio = repositorio.obtenerPorId(id)

                if (idEnEdicion != id) return@launch

                if (recordatorio != null) {
                    _formularioUiState.value = FormularioUiState(
                        titulo = recordatorio.titulo,
                        descripcion = recordatorio.descripcion ?: "",
                        categoria = recordatorio.categoria.nombre,
                        prioridad = recordatorio.prioridad
                    )
                } else {
                    _formularioUiState.value = FormularioUiState(
                        errorGuardado = "No se encontró el recordatorio"
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (idEnEdicion == id) {
                    _formularioUiState.value = FormularioUiState(
                        errorGuardado =
                            "No se pudo cargar el recordatorio"
                    )
                }
            }
        }
    }

    // Sirve tanto para crear como para editar
    fun guardarRecordatorio() {
        if (_formularioUiState.value.guardando) return

        val validado = erroresFormulario(
            _formularioUiState.value
        )

        _formularioUiState.value = validado

        if (!formularioValido()) return

        viewModelScope.launch {
            _formularioUiState.update {
                it.copy(
                    guardando = true,
                    guardadoExitoso = false,
                    errorGuardado = null
                )
            }

            try {
                val estado = _formularioUiState.value

                val categoria = listOf(
                    DatosSimulados.trabajo,
                    DatosSimulados.estudio,
                    DatosSimulados.personal
                ).first {
                    it.nombre == estado.categoria
                }

                val prioridad = requireNotNull(estado.prioridad)
                val idOriginal = idEnEdicion

                if (idOriginal != null) {
                    // Editar sin modificar ID, estado o fecha original
                    val original = repositorio.obtenerPorId(idOriginal)
                        ?: error("El recordatorio ya no existe")

                    val actualizado = original.copy(
                        titulo = estado.titulo.trim(),
                        descripcion = estado.descripcion.trim()
                            .ifBlank { null },
                        categoria = categoria,
                        prioridad = prioridad
                    )

                    val repoRoom =
                        repositorio as? RecordatoriosRepositoryRoom
                            ?: error("No está disponible la edición")

                    repoRoom.actualizarRecordatorio(actualizado)

                } else {
                    // Crear un registro nuevo
                    val nuevoId =
                        if (repositorio is RecordatoriosRepositoryRoom) {
                            0
                        } else {
                            (repositorio.obtenerRecordatorios()
                                .maxOfOrNull { it.id } ?: 0) + 1
                        }

                    val nuevo = Recordatorio(
                        id = nuevoId,
                        titulo = estado.titulo.trim(),
                        descripcion = estado.descripcion.trim()
                            .ifBlank { null },
                        categoria = categoria,
                        prioridad = prioridad,
                        estado = EstadoRecordatorio.Pendiente
                    )

                    repositorio.guardarRecordatorio(nuevo)
                }

                _formularioUiState.update {
                    it.copy(
                        guardando = false,
                        guardadoExitoso = true
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _formularioUiState.update {
                    it.copy(
                        guardando = false,
                        errorGuardado =
                            "No se pudieron guardar los cambios"
                    )
                }
            }
        }
    }

    // Cambiar entre pendiente y completado
    fun cambiarEstadoRecordatorio(
        id: Int,
        onResultado: (Recordatorio?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (repositorio is RecordatoriosRepositoryRoom) {
                    repositorio.cambiarEstado(id)
                }

                onResultado(repositorio.obtenerPorId(id))

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onResultado(null)
            }
        }
    }

    // Eliminar un recordatorio de Room
    fun eliminarRecordatorio(
        id: Int,
        onResultado: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val recordatorio = repositorio.obtenerPorId(id)

                if (
                    recordatorio != null &&
                    repositorio is RecordatoriosRepositoryRoom
                ) {
                    repositorio.eliminarRecordatorio(recordatorio)
                    onResultado(true)
                } else {
                    onResultado(false)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onResultado(false)
            }
        }
    }

    fun reiniciarFormulario() {
        idEnEdicion = null
        _formularioUiState.value = FormularioUiState()
    }

    fun obtenerRecordatorio(
        id: Int,
        onResultado: (Recordatorio?) -> Unit
    ) {
        viewModelScope.launch {
            onResultado(repositorio.obtenerPorId(id))
        }
    }
}
