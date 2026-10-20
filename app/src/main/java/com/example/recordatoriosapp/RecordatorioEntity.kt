
package com.example.recordatoriosapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordatorios")
data class RecordatorioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

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
    val pendienteSincronizacion: Boolean = true
)
