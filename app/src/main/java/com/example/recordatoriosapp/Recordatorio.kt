
package com.example.recordatoriosapp

// Prioridades de los recordatorios
sealed class Prioridad(val nombre: String) {
    data object Alta : Prioridad("Alta")
    data object Media : Prioridad("Media")
    data object Baja : Prioridad("Baja")
}

// Estados de los recordatorios
sealed class EstadoRecordatorio(val nombre: String) {
    data object Pendiente : EstadoRecordatorio("Pendiente")
    data object Completado : EstadoRecordatorio("Completado")
}

// Categoría de cada recordatorio
data class Categoria(
    val nombre: String,
    val color: String
)

// Modelo principal del recordatorio
data class Recordatorio(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String? = null,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: EstadoRecordatorio,
    val ubicacion: String? = null,
    val fechaAviso: String? = null
) {
    init {
        // ID 0: recordatorio nuevo, todavía sin guardar.
        // ID mayor que 0: recordatorio ya identificado.
        require(id >= 0) {
            "El ID no puede ser negativo"
        }

        require(titulo.isNotBlank()) {
            "El título no puede estar vacío"
        }
    }
}
