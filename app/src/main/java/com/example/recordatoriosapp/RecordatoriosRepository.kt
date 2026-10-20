
package com.example.recordatoriosapp

// Contrato para obtener y guardar recordatorios
interface RecordatoriosRepository {
    suspend fun obtenerRecordatorios(): List<Recordatorio>

    suspend fun obtenerPorId(id: Int): Recordatorio?

    suspend fun guardarRecordatorio(recordatorio: Recordatorio)
}

// Implementación temporal en memoria
class RecordatoriosRepositoryMemoria : RecordatoriosRepository {

    private val recordatorios = DatosSimulados.recordatorios
        .toMutableList()

    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        return recordatorios.toList()
    }

    override suspend fun obtenerPorId(id: Int): Recordatorio? {
        return recordatorios.find { it.id == id }
    }

    override suspend fun guardarRecordatorio(
        recordatorio: Recordatorio
    ) {
        require(recordatorios.none { it.id == recordatorio.id }) {
            "Ya existe un recordatorio con ese ID"
        }

        recordatorios.add(recordatorio)
    }
}
