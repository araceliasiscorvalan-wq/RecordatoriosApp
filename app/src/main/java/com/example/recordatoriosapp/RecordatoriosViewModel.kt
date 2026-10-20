
package com.example.recordatoriosapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        cargarRecordatorios()
    }

    fun cargarRecordatorios() {
        viewModelScope.launch {
            _listaUiState.value = ListaUiState.Cargando

            try {
                delay(600)

                val datos = repositorio.obtenerRecordatorios()

                _listaUiState.value = if (datos.isEmpty()) {
                    ListaUiState.Vacio
                } else {
                    ListaUiState.ConDatos(datos)
                }
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
            it.copy(descripcion = valor, errorDescripcion = null)
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
                delay(700)

                val estado = _formularioUiState.value
                val existentes = repositorio.obtenerRecordatorios()
                val nuevoId = (existentes.maxOfOrNull {
                    it.id
                } ?: 0) + 1

                val categoria = listOf(
                    DatosSimulados.trabajo,
                    DatosSimulados.estudio,
                    DatosSimulados.personal
                ).first { it.nombre == estado.categoria }

                val nuevo = Recordatorio(
                    id = nuevoId,
                    titulo = estado.titulo.trim(),
                    descripcion = estado.descripcion.trim()
                        .ifBlank { null },
                    categoria = categoria,
                    prioridad = requireNotNull(estado.prioridad),
                    estado = EstadoRecordatorio.Pendiente
                )

                repositorio.guardarRecordatorio(nuevo)

                _formularioUiState.update {
                    it.copy(
                        guardando = false,
                        guardadoExitoso = true
                    )
                }

                cargarRecordatorios()

            } catch (e: Exception) {
                _formularioUiState.update {
                    it.copy(
                        guardando = false,
                        errorGuardado = "No se pudo guardar el recordatorio"
                    )
                }
            }
        }
    }

    fun reiniciarFormulario() {
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
