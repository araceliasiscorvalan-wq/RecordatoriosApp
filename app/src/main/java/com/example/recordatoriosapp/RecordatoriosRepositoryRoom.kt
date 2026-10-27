
package com.example.recordatoriosapp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RecordatoriosRepositoryRoom(
    private val dao: RecordatorioDao
) : RecordatoriosRepository {

    // Observar los recordatorios guardados en Room
    val recordatoriosFlow: Flow<List<Recordatorio>> =
        dao.obtenerTodos().map { entidades ->
            entidades.map { it.aRecordatorio() }
        }

    // Consultar todos los recordatorios
    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        return recordatoriosFlow.first()
    }

    // Buscar un recordatorio por su ID
    override suspend fun obtenerPorId(id: Int): Recordatorio? {
        return dao.obtenerPorId(id)
            .first()
            ?.aRecordatorio()
    }

    // Guardar un nuevo recordatorio
    override suspend fun guardarRecordatorio(
        recordatorio: Recordatorio
    ) {
        dao.insertar(recordatorio.aEntity())
    }

    // Actualizar sin perder la fecha original
    suspend fun actualizarRecordatorio(
        recordatorio: Recordatorio
    ) {
        val entidadActual = dao.obtenerPorId(recordatorio.id)
            .first() ?: return

        val entidadActualizada = entidadActual.copy(
            titulo = recordatorio.titulo,
            descripcion = recordatorio.descripcion,
            categoria = recordatorio.categoria.nombre,
            prioridad = recordatorio.prioridad.nombre,
            estado = recordatorio.estado.nombre,
            ubicacion = recordatorio.ubicacion,
            fechaAviso = recordatorio.fechaAviso,
            pendienteSincronizacion = true
        )

        dao.actualizar(entidadActualizada)
    }

    // Cambiar entre pendiente y completado
    suspend fun cambiarEstado(id: Int) {
        val entidadActual = dao.obtenerPorId(id)
            .first() ?: return

        val nuevoEstado = if (
            entidadActual.estado == "Completado"
        ) {
            "Pendiente"
        } else {
            "Completado"
        }

        dao.actualizar(
            entidadActual.copy(
                estado = nuevoEstado,
                pendienteSincronizacion = true
            )
        )
    }

    // Eliminar un recordatorio
    suspend fun eliminarRecordatorio(
        recordatorio: Recordatorio
    ) {
        val entidadActual = dao.obtenerPorId(recordatorio.id)
            .first() ?: return

        dao.eliminar(entidadActual)
    }

    // Obtener cambios pendientes de sincronización
    suspend fun obtenerPendientes(): List<RecordatorioEntity> {
        return dao.obtenerPendientes()
    }
}
