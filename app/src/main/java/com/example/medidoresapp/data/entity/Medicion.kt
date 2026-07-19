package com.example.medidoresapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "mediciones")
data class Medicion(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val tipoServicio: String,

    val lectura: Double,

    val fecha: String

)