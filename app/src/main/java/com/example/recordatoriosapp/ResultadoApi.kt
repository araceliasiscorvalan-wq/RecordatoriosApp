
package com.example.recordatoriosapp

import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException

// Representa el resultado de una petición HTTP
sealed class ResultadoApi<out T> {

    data class Exito<T>(
        val datos: T
    ) : ResultadoApi<T>()

    data class ErrorHttp(
        val codigo: Int,
        val mensaje: String
    ) : ResultadoApi<Nothing>()

    data class ErrorConexion(
        val mensaje: String
    ) : ResultadoApi<Nothing>()

    data class ErrorDesconocido(
        val mensaje: String
    ) : ResultadoApi<Nothing>()
}

// Ejecuta una petición Retrofit y maneja los errores
suspend fun <T : Any> ejecutarPeticion(
    peticion: suspend () -> Response<T>
): ResultadoApi<T> {

    return try {
        val respuesta = peticion()

        if (respuesta.isSuccessful) {
            val datos = respuesta.body()

            if (datos != null) {
                ResultadoApi.Exito(datos)
            } else {
                ResultadoApi.ErrorHttp(
                    codigo = respuesta.code(),
                    mensaje = "El servidor devolvió una respuesta vacía"
                )
            }
        } else {
            ResultadoApi.ErrorHttp(
                codigo = respuesta.code(),
                mensaje = when (respuesta.code()) {
                    400 -> "Solicitud incorrecta"
                    401 -> "No autorizado"
                    403 -> "Acceso denegado"
                    404 -> "Recurso no encontrado"
                    409 -> "Conflicto con los datos existentes"
                    429 -> "Demasiadas solicitudes"
                    in 500..599 -> "Error del servidor"
                    else -> "Error HTTP ${respuesta.code()}"
                }
            )
        }

    } catch (e: CancellationException) {
        // Respetar la cancelación de corrutinas
        throw e

    } catch (e: IOException) {
        ResultadoApi.ErrorConexion(
            mensaje = "No se pudo conectar con el servidor"
        )

    } catch (e: Exception) {
        ResultadoApi.ErrorDesconocido(
            mensaje = "Ocurrió un error inesperado"
        )
    }
}
