package com.example.medidoresapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Esta es la clase que define qué datos vamos a guardar de cada medición.
 * Es como el "molde" de la información en la base de datos.
 */
@Entity(tableName = "mediciones")
data class Medicion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,             // El ID es automático para no preocuparnos por él
    val tipoServicio: String,    // Aquí va si es Agua, Luz o Gas
    val lectura: Double,         // El valor numérico que marca el medidor
    val fecha: String            // La fecha guardada como texto (DD-MM-YYYY)
)
