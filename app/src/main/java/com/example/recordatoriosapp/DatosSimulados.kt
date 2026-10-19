
package com.example.recordatoriosapp

object DatosSimulados {

    // Categorías disponibles
    val trabajo = Categoria("Trabajo", "Azul")
    val estudio = Categoria("Estudio", "Verde")
    val personal = Categoria("Personal", "Rosa")

    // Lista de recordatorios simulados
    val recordatorios = listOf(
        Recordatorio(
            id = 1,
            titulo = "Estudiar Kotlin",
            descripcion = "Repasar las clases de Kotlin",
            categoria = estudio,
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.Pendiente
        ),
        Recordatorio(
            id = 2,
            titulo = "Reunión de trabajo",
            descripcion = "Reunión con el equipo",
            categoria = trabajo,
            prioridad = Prioridad.Media,
            estado = EstadoRecordatorio.Pendiente
        ),
        Recordatorio(
            id = 3,
            titulo = "Comprar alimentos",
            categoria = personal,
            prioridad = Prioridad.Baja,
            estado = EstadoRecordatorio.Completado
        ),
        Recordatorio(
            id = 4,
            titulo = "Entregar actividad Mobile",
            categoria = estudio,
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.Pendiente,
            fechaAviso = "2026-10-25"
        )
    )

    // Set: evita categorías repetidas
    val categorias = recordatorios
        .map { it.categoria.nombre }
        .toSet()

    // Map: relaciona categorías con colores
    val coloresCategorias = mapOf(
        "Trabajo" to "Azul",
        "Estudio" to "Verde",
        "Personal" to "Rosa"
    )

    // Filtrar recordatorios pendientes
    val pendientes = recordatorios.filter {
        it.estado == EstadoRecordatorio.Pendiente
    }

    // Filtrar recordatorios de prioridad alta
    val prioridadAlta = recordatorios.filter {
        it.prioridad == Prioridad.Alta
    }
}
