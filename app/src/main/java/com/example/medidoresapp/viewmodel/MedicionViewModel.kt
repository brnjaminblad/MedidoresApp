package com.example.medidoresapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medidoresapp.data.entity.Medicion
import com.example.medidoresapp.data.repository.MedicionRepository
import kotlinx.coroutines.launch

class MedicionViewModel(
    private val repository: MedicionRepository
) : ViewModel() {


    fun guardarMedicion(medicion: Medicion) {

        viewModelScope.launch {

            repository.insertarMedicion(medicion)

        }

    }


    fun obtenerMediciones(
        onResult: (List<Medicion>) -> Unit
    ) {

        viewModelScope.launch {

            val lista = repository.obtenerTodasLasMediciones()

            onResult(lista)

        }

    }


    fun eliminarMedicion(medicion: Medicion) {

        viewModelScope.launch {

            repository.eliminarMedicion(medicion)

        }

    }


}