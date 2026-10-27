
package com.example.recordatoriosapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordatorioDao {

    // Obtener todos los recordatorios
    @Query("SELECT * FROM recordatorios ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<RecordatorioEntity>>

    // Obtener un recordatorio por identificador
    @Query("SELECT * FROM recordatorios WHERE id = :id LIMIT 1")
    fun obtenerPorId(id: Int): Flow<RecordatorioEntity?>

    // Filtrar recordatorios por estado
    @Query("SELECT * FROM recordatorios WHERE estado = :estado ORDER BY fecha DESC")
    fun obtenerPorEstado(
        estado: String
    ): Flow<List<RecordatorioEntity>>

    // Filtrar recordatorios por prioridad
    @Query("SELECT * FROM recordatorios WHERE prioridad = :prioridad ORDER BY fecha DESC")
    fun obtenerPorPrioridad(
        prioridad: String
    ): Flow<List<RecordatorioEntity>>

    // Insertar un recordatorio
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(
        recordatorio: RecordatorioEntity
    ): Long

    // Actualizar un recordatorio existente
    @Update
    suspend fun actualizar(
        recordatorio: RecordatorioEntity
    ): Int

    // Eliminar un recordatorio
    @Delete
    suspend fun eliminar(
        recordatorio: RecordatorioEntity
    ): Int

    // Obtener registros pendientes de sincronización
    @Query(
        "SELECT * FROM recordatorios " +
                "WHERE pendienteSincronizacion = 1"
    )
    suspend fun obtenerPendientes(): List<RecordatorioEntity>
}
