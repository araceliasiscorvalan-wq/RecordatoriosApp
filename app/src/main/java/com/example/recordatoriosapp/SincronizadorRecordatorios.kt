
package com.example.recordatoriosapp

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException

class SincronizadorRecordatorios(
    private val dao: RecordatorioDao,
    private val api: RecordatoriosApiService
) {

    private val mutex = Mutex()

    suspend fun sincronizar(): Int = mutex.withLock {
        var sincronizados = 0

        val pendientes = dao.obtenerPendientes()

        for (registro in pendientes) {
            try {
                if (registro.eliminado) {

                    val respuesta = api.eliminarRecordatorio(
                        registro.clienteId
                    )

                    if (!respuesta.isSuccessful) {
                        throw IllegalStateException(
                            "Error HTTP ${respuesta.code()} " +
                                    "al eliminar ${registro.clienteId}"
                        )
                    }

                    val confirmados =
                        dao.confirmarSincronizacionSiNoCambio(
                            id = registro.id,
                            clienteId = registro.clienteId,
                            servidorId = registro.servidorId,
                            titulo = registro.titulo,
                            descripcion = registro.descripcion,
                            categoria = registro.categoria,
                            prioridad = registro.prioridad,
                            estado = registro.estado,
                            ubicacion = registro.ubicacion,
                            fechaAviso = registro.fechaAviso,
                            eliminado = true
                        )

                    if (confirmados > 0) {
                        dao.limpiarEliminadoSincronizado(
                            registro.id
                        )
                        sincronizados++
                    }

                } else {

                    val dto = RecordatorioDto(
                        clienteId = registro.clienteId,
                        titulo = registro.titulo,
                        descripcion = registro.descripcion,
                        categoria = registro.categoria,
                        prioridad = registro.prioridad,
                        estado = registro.estado,
                        fecha = registro.fecha
                    )

                    val respuesta = api.guardarRecordatorio(
                        registro.clienteId,
                        dto
                    )

                    if (!respuesta.isSuccessful) {
                        throw IllegalStateException(
                            "Error HTTP ${respuesta.code()} " +
                                    "al guardar ${registro.clienteId}"
                        )
                    }

                    val servidorId = respuesta.body()?.id

                    val confirmados =
                        dao.confirmarSincronizacionSiNoCambio(
                            id = registro.id,
                            clienteId = registro.clienteId,
                            servidorId = servidorId,
                            titulo = registro.titulo,
                            descripcion = registro.descripcion,
                            categoria = registro.categoria,
                            prioridad = registro.prioridad,
                            estado = registro.estado,
                            ubicacion = registro.ubicacion,
                            fechaAviso = registro.fechaAviso,
                            eliminado = false
                        )

                    if (confirmados > 0) {
                        sincronizados++
                    }
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: IOException) {
                throw IllegalStateException(
                    "Error de conexión: " +
                            (e.message ?: "No se pudo conectar con la API"),
                    e
                )
            }
        }

        sincronizados
    }
}
