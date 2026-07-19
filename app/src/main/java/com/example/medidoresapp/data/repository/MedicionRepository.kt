package com.example.medidoresapp.data.repository

import com.example.medidoresapp.data.dao.MedicionDao
import com.example.medidoresapp.data.entity.Medicion

/**
 * El Repositorio es como el "mensajero" entre el código de la app y la base de datos.
 * Su trabajo es simplemente pedir o guardar los datos que necesitamos.
 */
class MedicionRepository(private val medicionDao: MedicionDao) {

    // Trae todas las mediciones que tenemos guardadas
    suspend fun obtenerTodasLasMediciones() = medicionDao.obtenerTodasLasMediciones()

    // Busca una medición específica si sabemos su ID
    suspend fun obtenerMedicionPorId(id: Int) = medicionDao.obtenerMedicionPorId(id)

    // Guarda una nueva medición
    suspend fun insertarMedicion(medicion: Medicion) = medicionDao.insertarMedicion(medicion)

    // Actualiza los datos de una medición que ya existía
    suspend fun actualizarMedicion(medicion: Medicion) = medicionDao.actualizarMedicion(medicion)

    // Borra una medición de la lista
    suspend fun eliminarMedicion(medicion: Medicion) = medicionDao.eliminarMedicion(medicion)
}
