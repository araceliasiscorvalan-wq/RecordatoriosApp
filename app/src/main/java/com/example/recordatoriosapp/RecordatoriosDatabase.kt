
package com.example.recordatoriosapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [RecordatorioEntity::class],
    version = 2,
    exportSchema = false
)
abstract class RecordatoriosDatabase : RoomDatabase() {

    abstract fun recordatorioDao(): RecordatorioDao

    companion object {

        @Volatile
        private var INSTANCE: RecordatoriosDatabase? = null

        // Migración desde la versión 1 a la versión 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {

                // Crear la nueva estructura de la tabla
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS recordatorios_nueva (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        clienteId TEXT NOT NULL,
                        servidorId TEXT,
                        titulo TEXT NOT NULL,
                        descripcion TEXT,
                        categoria TEXT NOT NULL,
                        prioridad TEXT NOT NULL,
                        estado TEXT NOT NULL,
                        fecha INTEGER NOT NULL,
                        ubicacion TEXT,
                        fechaAviso TEXT,
                        pendienteSincronizacion INTEGER NOT NULL,
                        eliminado INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                // Copiar los registros existentes sin perderlos
                // y asignar un identificador a cada uno
                db.execSQL(
                    """
                    INSERT INTO recordatorios_nueva (
                        id,
                        clienteId,
                        servidorId,
                        titulo,
                        descripcion,
                        categoria,
                        prioridad,
                        estado,
                        fecha,
                        ubicacion,
                        fechaAviso,
                        pendienteSincronizacion,
                        eliminado
                    )
                    SELECT
                        id,
                        lower(hex(randomblob(16))),
                        NULL,
                        titulo,
                        descripcion,
                        categoria,
                        prioridad,
                        estado,
                        fecha,
                        ubicacion,
                        fechaAviso,
                        pendienteSincronizacion,
                        0
                    FROM recordatorios
                    """.trimIndent()
                )

                // Reemplazar la tabla anterior
                db.execSQL("DROP TABLE recordatorios")

                db.execSQL(
                    """
                    ALTER TABLE recordatorios_nueva
                    RENAME TO recordatorios
                    """.trimIndent()
                )
            }
        }

        fun obtenerInstancia(
            context: Context
        ): RecordatoriosDatabase {

            return INSTANCE ?: synchronized(this) {

                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    RecordatoriosDatabase::class.java,
                    "recordatorios_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instancia
                instancia
            }
        }
    }
}
