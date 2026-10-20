
package com.example.recordatoriosapp

// Estados posibles de la pantalla de listado
sealed interface ListaUiState {

    data object Cargando : ListaUiState

    data object Vacio : ListaUiState

    data class ConDatos(
        val recordatorios: List<Recordatorio>
    ) : ListaUiState

    data class Error(
        val mensaje: String
    ) : ListaUiState
}

// Estado del formulario de creación
data class FormularioUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val prioridad: Prioridad? = null,

    // Mensajes de validación
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorCategoria: String? = null,
    val errorPrioridad: String? = null,

    // Estado del guardado
    val guardando: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val errorGuardado: String? = null
)
