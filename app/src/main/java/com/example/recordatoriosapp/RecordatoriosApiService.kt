
package com.example.recordatoriosapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Representa un recordatorio enviado o recibido
// mediante JSON desde un servidor.
data class RecordatorioDto(
    val id: String? = null,
    val clienteId: String,
    val titulo: String,
    val descripcion: String? = null,
    val categoria: String,
    val prioridad: String,
    val estado: String
)

// Define las operaciones HTTP de la API.
interface RecordatoriosApiService {

    // GET: consultar recordatorios
    @GET("recordatorios")
    suspend fun obtenerRecordatorios():
            Response<List<RecordatorioDto>>

    // GET: consultar un recordatorio específico
    @GET("recordatorios/{id}")
    suspend fun obtenerPorId(
        @Path("id") id: String
    ): Response<RecordatorioDto>

    // POST: crear un recordatorio
    @POST("recordatorios")
    suspend fun crearRecordatorio(
        @Body recordatorio: RecordatorioDto
    ): Response<RecordatorioDto>

    // PUT: actualizar un recordatorio
    @PUT("recordatorios/{id}")
    suspend fun actualizarRecordatorio(
        @Path("id") id: String,
        @Body recordatorio: RecordatorioDto
    ): Response<RecordatorioDto>

    // DELETE: eliminar un recordatorio
    @DELETE("recordatorios/{id}")
    suspend fun eliminarRecordatorio(
        @Path("id") id: String
    ): Response<Unit>
}
