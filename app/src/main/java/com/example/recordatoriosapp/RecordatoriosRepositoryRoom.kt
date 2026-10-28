
package com.example.recordatoriosapp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RecordatoriosRepositoryRoom(
    private val dao: RecordatorioDao
) : RecordatoriosRepository {

    // Observar solamente los recordatorios activos
    val recordatoriosFlow: Flow<List<Recordatorio>> =
        dao.obtenerTodos().map { entidades ->
            entidades.map { it.aRecordatorio() }
        }

    // Consultar los recordatorios activos
    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        return recordatoriosFlow.first()
    }

    // Buscar un recordatorio por su ID
    override suspend fun obtenerPorId(id: Int): Recordatorio? {
        return dao.obtenerPorId(id)
            .first()
            ?.aRecordatorio()
    }

    // Crear un recordatorio nuevo
    override suspend fun guardarRecordatorio(
        recordatorio: Recordatorio
    ) {
        dao.insertar(recordatorio.aEntity())
    }

    // Editar conservando los identificadores y la fecha
    suspend fun actualizarRecordatorio(
        recordatorio: Recordatorio
    ) {
        val entidadActual = dao.obtenerRegistroPorId(
            recordatorio.id
        ) ?: return

        if (entidadActual.eliminado) return

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
        val entidadActual = dao.obtenerRegistroPorId(id)
            ?: return

        if (entidadActual.eliminado) return

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

    // Eliminación lógica:
    // ocultar la tarea sin perder los datos
    // necesarios para sincronizar su eliminación.
    suspend fun eliminarRecordatorio(
        recordatorio: Recordatorio
    ) {
        dao.marcarEliminado(recordatorio.id)
    }

    // Recuperar los cambios pendientes
    suspend fun obtenerPendientes(): List<RecordatorioEntity> {
        return dao.obtenerPendientes()
    }
}
