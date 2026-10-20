
package com.example.recordatoriosapp

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// Una sola instancia de DataStore para toda la aplicación
private val Context.dataStore by preferencesDataStore(
    name = "preferencias_recordatorios"
)

// Modelo inmutable de las preferencias
data class PreferenciasApp(
    val temaOscuro: Boolean = false,
    val mostrarCompletados: Boolean = true,
    val ordenamiento: String = "fecha"
)

class PreferenciasRepository(
    private val context: Context
) {
    private object Claves {
        val TEMA_OSCURO = booleanPreferencesKey("tema_oscuro")
        val MOSTRAR_COMPLETADOS =
            booleanPreferencesKey("mostrar_completados")
        val ORDENAMIENTO = stringPreferencesKey("ordenamiento")
    }

    val preferencias: Flow<PreferenciasApp> =
        context.dataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(
                        androidx.datastore.preferences.core
                            .emptyPreferences()
                    )
                } else {
                    throw error
                }
            }
            .map { datos ->
                val ordenGuardado =
                    datos[Claves.ORDENAMIENTO] ?: "fecha"

                PreferenciasApp(
                    temaOscuro =
                        datos[Claves.TEMA_OSCURO] ?: false,

                    mostrarCompletados =
                        datos[Claves.MOSTRAR_COMPLETADOS] ?: true,

                    ordenamiento = ordenGuardado.takeIf {
                        it in setOf("fecha", "titulo", "prioridad")
                    } ?: "fecha"
                )
            }

    suspend fun cambiarTemaOscuro(activado: Boolean) {
        context.dataStore.edit { datos ->
            datos[Claves.TEMA_OSCURO] = activado
        }
    }

    suspend fun cambiarMostrarCompletados(mostrar: Boolean) {
        context.dataStore.edit { datos ->
            datos[Claves.MOSTRAR_COMPLETADOS] = mostrar
        }
    }

    suspend fun cambiarOrdenamiento(orden: String) {
        require(orden in setOf("fecha", "titulo", "prioridad")) {
            "Ordenamiento no válido"
        }

        context.dataStore.edit { datos ->
            datos[Claves.ORDENAMIENTO] = orden
        }
    }
}
