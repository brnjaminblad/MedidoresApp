package com.example.medidoresapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medidoresapp.data.entity.Medicion
import com.example.medidoresapp.data.repository.MedicionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * ViewModel encargado de gestionar la lógica de presentación y el ordenamiento de fechas DD-MM-YYYY.
 */
class MedicionViewModel(
    private val repository: MedicionRepository
) : ViewModel() {

    private val _mediciones = MutableLiveData<List<Medicion>>()
    val mediciones: LiveData<List<Medicion>> get() = _mediciones

    // Orden inicial: descendente (más recientes primero)
    private var ordenDescendente = true
    
    // Formateador para convertir "DD-MM-YYYY" a un objeto Date comparable
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    init {
        obtenerMediciones()
    }

    fun obtenerMediciones() {
        viewModelScope.launch {
            val lista = withContext(Dispatchers.IO) {
                repository.obtenerTodasLasMediciones()
            }
            aplicarOrden(lista)
        }
    }

    fun alternarOrden() {
        ordenDescendente = !ordenDescendente
        _mediciones.value?.let { aplicarOrden(it) }
    }

    /**
     * Aplica el ordenamiento convirtiendo el string DD-MM-YYYY a Date para una comparación correcta.
     */
    private fun aplicarOrden(lista: List<Medicion>) {
        val listaOrdenada = if (ordenDescendente) {
            // Recientes primero: ordenamos por el tiempo en milisegundos de forma descendente
            lista.sortedByDescending { parseDate(it.fecha) }
        } else {
            // Antiguos primero: ordenamos de forma ascendente
            lista.sortedBy { parseDate(it.fecha) }
        }
        _mediciones.value = listaOrdenada
    }

    /**
     * Convierte el string de fecha a milisegundos para poder comparar correctamente.
     * Si el formato falla, devuelve 0 para evitar errores.
     */
    private fun parseDate(fechaStr: String): Long {
        return try {
            dateFormat.parse(fechaStr)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    fun guardarMedicion(medicion: Medicion) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (medicion.id == 0) {
                    repository.insertarMedicion(medicion)
                } else {
                    repository.actualizarMedicion(medicion)
                }
            }
            obtenerMediciones() 
        }
    }

    fun eliminarMedicion(medicion: Medicion) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.eliminarMedicion(medicion)
            }
            obtenerMediciones()
        }
    }
}
