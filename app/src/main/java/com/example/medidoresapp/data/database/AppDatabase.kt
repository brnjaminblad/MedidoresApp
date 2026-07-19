package com.example.medidoresapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.medidoresapp.data.dao.MedicionDao
import com.example.medidoresapp.data.entity.Medicion

/**
 * Configuración de la base de datos de Room.
 * Define las entidades y la versión de la base de datos.
 */
@Database(
    entities = [Medicion::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Acceso al DAO de mediciones
    abstract fun medicionDao(): MedicionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Implementación del patrón Singleton para garantizar una única instancia
         * de la base de datos en toda la aplicación.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medidores_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
