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
 * Tipos de ordenamiento disponibles.
 */
enum class SortType {
    DATE_DESC, // Recientes primero
    DATE_ASC,  // Antiguos primero
    VALUE_DESC, // Lectura mayor a menor
    VALUE_ASC,  // Lectura menor a mayor
    SERVICE_TYPE // Por tipo de servicio
}

/**
 * ViewModel encargado de gestionar la lógica de presentación y el ordenamiento.
 */
class MedicionViewModel(
    private val repository: MedicionRepository
) : ViewModel() {

    private val _mediciones = MutableLiveData<List<Medicion>>()
    val mediciones: LiveData<List<Medicion>> get() = _mediciones

    private var currentSort = SortType.DATE_DESC
    
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

    fun setSortType(sortType: SortType) {
        currentSort = sortType
        _mediciones.value?.let { aplicarOrden(it) }
    }

    private fun aplicarOrden(lista: List<Medicion>) {
        val listaOrdenada = when (currentSort) {
            SortType.DATE_DESC -> lista.sortedByDescending { parseDate(it.fecha) }
            SortType.DATE_ASC -> lista.sortedBy { parseDate(it.fecha) }
            SortType.VALUE_DESC -> lista.sortedByDescending { it.lectura }
            SortType.VALUE_ASC -> lista.sortedBy { it.lectura }
            SortType.SERVICE_TYPE -> lista.sortedWith(
                compareBy<Medicion> { it.tipoServicio }.thenByDescending { parseDate(it.fecha) }
            )
        }
        _mediciones.value = listaOrdenada
    }

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
