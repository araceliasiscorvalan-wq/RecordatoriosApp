
package com.example.recordatoriosapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

// Datos intercambiados con el servidor mediante JSON
data class RecordatorioDto(
    val id: String? = null,
    val clienteId: String,
    val titulo: String,
    val descripcion: String? = null,
    val categoria: String,
    val prioridad: String,
    val estado: String,
    val fecha: Long? = null
)

// Contrato HTTP de la API de recordatorios
interface RecordatoriosApiService {

    // Consultar todos los recordatorios del servidor
    @GET("recordatorios")
    suspend fun obtenerRecordatorios():
            Response<List<RecordatorioDto>>

    // Consultar un registro por su identificador estable
    @GET("recordatorios/{clienteId}")
    suspend fun obtenerPorClienteId(
        @Path("clienteId") clienteId: String
    ): Response<RecordatorioDto>

    // Crear o actualizar usando siempre el mismo clienteId.
    // El servidor deberá implementar esta operación
    // como idempotente (upsert).
    @PUT("recordatorios/{clienteId}")
    suspend fun guardarRecordatorio(
        @Path("clienteId") clienteId: String,
        @Body recordatorio: RecordatorioDto
    ): Response<RecordatorioDto>

    // El servidor deberá considerar exitosa una
    // eliminación repetida del mismo identificador.
    @DELETE("recordatorios/{clienteId}")
    suspend fun eliminarRecordatorio(
        @Path("clienteId") clienteId: String
    ): Response<Unit>
}
