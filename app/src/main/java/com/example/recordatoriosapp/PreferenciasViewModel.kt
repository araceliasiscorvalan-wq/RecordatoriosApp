
package com.example.recordatoriosapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferenciasViewModel(
    private val repositorio: PreferenciasRepository
) : ViewModel() {

    // Preferencias observables desde Compose
    val preferencias: StateFlow<PreferenciasApp> =
        repositorio.preferencias.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PreferenciasApp()
        )

    // Cambiar entre tema claro y oscuro
    fun cambiarTemaOscuro(activado: Boolean) {
        viewModelScope.launch {
            repositorio.cambiarTemaOscuro(activado)
        }
    }

    // Mostrar u ocultar tareas completadas
    fun cambiarMostrarCompletados(mostrar: Boolean) {
        viewModelScope.launch {
            repositorio.cambiarMostrarCompletados(mostrar)
        }
    }

    // Cambiar el orden de los recordatorios
    fun cambiarOrdenamiento(orden: String) {
        viewModelScope.launch {
            repositorio.cambiarOrdenamiento(orden)
        }
    }
}
