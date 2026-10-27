
package com.example.recordatoriosapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "recordatorios")
data class RecordatorioEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Identificador único del dispositivo
    val clienteId: String = UUID.randomUUID().toString(),

    // Identificador del servidor
    val servidorId: String? = null,

    val titulo: String,
    val descripcion: String? = null,
    val categoria: String,
    val prioridad: String,
    val estado: String,

    val fecha: Long = System.currentTimeMillis(),

    // Datos opcionales
    val ubicacion: String? = null,
    val fechaAviso: String? = null,

    // Control de sincronización
    val pendienteSincronizacion: Boolean = true,

    // Eliminación pendiente de sincronizar
    val eliminado: Boolean = false
)
