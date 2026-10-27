
package com.example.recordatoriosapp

// Convertir el modelo de la aplicación a la entidad de Room
fun Recordatorio.aEntity(): RecordatorioEntity {
    return RecordatorioEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        categoria = categoria.nombre,
        prioridad = prioridad.nombre,
        estado = estado.nombre,
        ubicacion = ubicacion,
        fechaAviso = fechaAviso,
        pendienteSincronizacion = true
    )
}

// Convertir la entidad de Room al modelo de la aplicación
fun RecordatorioEntity.aRecordatorio(): Recordatorio {

    val categoriaModelo = when (categoria) {
        "Trabajo" -> DatosSimulados.trabajo
        "Estudio" -> DatosSimulados.estudio
        "Personal" -> DatosSimulados.personal
        else -> Categoria(categoria, "Gris")
    }

    val prioridadModelo = when (prioridad) {
        "Alta" -> Prioridad.Alta
        "Media" -> Prioridad.Media
        "Baja" -> Prioridad.Baja
        else -> Prioridad.Media
    }

    val estadoModelo = when (estado) {
        "Completado" -> EstadoRecordatorio.Completado
        else -> EstadoRecordatorio.Pendiente
    }

    return Recordatorio(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        categoria = categoriaModelo,
        prioridad = prioridadModelo,
        estado = estadoModelo,
        ubicacion = ubicacion,
        fechaAviso = fechaAviso
    )
}
