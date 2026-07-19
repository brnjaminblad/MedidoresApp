package com.example.medidoresapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.medidoresapp.data.entity.Medicion

@Dao
interface MedicionDao {

    @Insert
    suspend fun insertarMedicion(medicion: Medicion)

    @Update
    suspend fun actualizarMedicion(medicion: Medicion)

    @Delete
    suspend fun eliminarMedicion(medicion: Medicion)

    @Query("SELECT * FROM mediciones")
    suspend fun obtenerTodasLasMediciones(): List<Medicion>

    @Query("SELECT * FROM mediciones WHERE id = :id")
    suspend fun obtenerMedicionPorId(id: Int): Medicion?

}