
package com.example.recordatoriosapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordatorioDao {

    @Query(
        "SELECT * FROM recordatorios " +
                "WHERE eliminado = 0 ORDER BY fecha DESC"
    )
    fun obtenerTodos(): Flow<List<RecordatorioEntity>>

    @Query(
        "SELECT * FROM recordatorios " +
                "WHERE id = :id AND eliminado = 0 LIMIT 1"
    )
    fun obtenerPorId(id: Int): Flow<RecordatorioEntity?>

    @Query(
        "SELECT * FROM recordatorios " +
                "WHERE id = :id LIMIT 1"
    )
    suspend fun obtenerRegistroPorId(
        id: Int
    ): RecordatorioEntity?

    @Query(
        "SELECT * FROM recordatorios " +
                "WHERE clienteId = :clienteId LIMIT 1"
    )
    suspend fun obtenerPorClienteId(
        clienteId: String
    ): RecordatorioEntity?

    @Query(
        "SELECT * FROM recordatorios " +
                "WHERE estado = :estado AND eliminado = 0 " +
                "ORDER BY fecha DESC"
    )
    fun obtenerPorEstado(
        estado: String
    ): Flow<List<RecordatorioEntity>>

    @Query(
        "SELECT * FROM recordatorios " +
                "WHERE prioridad = :prioridad AND eliminado = 0 " +
                "ORDER BY fecha DESC"
    )
    fun obtenerPorPrioridad(
        prioridad: String
    ): Flow<List<RecordatorioEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(
        recordatorio: RecordatorioEntity
    ): Long

    @Update
    suspend fun actualizar(
        recordatorio: RecordatorioEntity
    ): Int

    @Query(
        "UPDATE recordatorios " +
                "SET eliminado = 1, pendienteSincronizacion = 1 " +
                "WHERE id = :id AND eliminado = 0"
    )
    suspend fun marcarEliminado(id: Int): Int

    @Query(
        "SELECT * FROM recordatorios " +
                "WHERE pendienteSincronizacion = 1 " +
                "ORDER BY fecha ASC"
    )
    suspend fun obtenerPendientes(): List<RecordatorioEntity>

    @Query(
        "UPDATE recordatorios " +
                "SET pendienteSincronizacion = 0 " +
                "WHERE id = :id"
    )
    suspend fun marcarSincronizado(id: Int): Int

    @Query(
        "UPDATE recordatorios " +
                "SET servidorId = :servidorId " +
                "WHERE id = :id"
    )
    suspend fun actualizarServidorId(
        id: Int,
        servidorId: String
    ): Int

    // Confirmar la sincronización solamente si el registro
    // conserva los mismos datos enviados al servidor.
    @Query(
        """
        UPDATE recordatorios
        SET pendienteSincronizacion = 0,
            servidorId = :servidorId
        WHERE id = :id
          AND clienteId = :clienteId
          AND titulo = :titulo
          AND descripcion IS :descripcion
          AND categoria = :categoria
          AND prioridad = :prioridad
          AND estado = :estado
          AND ubicacion IS :ubicacion
          AND fechaAviso IS :fechaAviso
          AND eliminado = :eliminado
          AND pendienteSincronizacion = 1
        """
    )
    suspend fun confirmarSincronizacionSiNoCambio(
        id: Int,
        clienteId: String,
        servidorId: String?,
        titulo: String,
        descripcion: String?,
        categoria: String,
        prioridad: String,
        estado: String,
        ubicacion: String?,
        fechaAviso: String?,
        eliminado: Boolean
    ): Int

    @Query(
        "DELETE FROM recordatorios " +
                "WHERE id = :id AND eliminado = 1 " +
                "AND pendienteSincronizacion = 0"
    )
    suspend fun limpiarEliminadoSincronizado(id: Int): Int
}
