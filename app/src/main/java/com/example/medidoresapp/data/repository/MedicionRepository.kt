package com.example.medidoresapp.data.repository

import com.example.medidoresapp.data.dao.MedicionDao
import com.example.medidoresapp.data.entity.Medicion

class MedicionRepository(
    private val medicionDao: MedicionDao
) {

    suspend fun insertarMedicion(medicion: Medicion) {
        medicionDao.insertarMedicion(medicion)
    }

    suspend fun actualizarMedicion(medicion: Medicion) {
        medicionDao.actualizarMedicion(medicion)
    }

    suspend fun eliminarMedicion(medicion: Medicion) {
        medicionDao.eliminarMedicion(medicion)
    }

    suspend fun obtenerTodasLasMediciones(): List<Medicion> {
        return medicionDao.obtenerTodasLasMediciones()
    }

    suspend fun obtenerMedicionPorId(id: Int): Medicion? {
        return medicionDao.obtenerMedicionPorId(id)
    }
}