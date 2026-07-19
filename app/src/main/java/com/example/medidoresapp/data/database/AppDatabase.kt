package com.example.medidoresapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.medidoresapp.data.dao.MedicionDao
import com.example.medidoresapp.data.entity.Medicion

@Database(
    entities = [Medicion::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicionDao(): MedicionDao

}