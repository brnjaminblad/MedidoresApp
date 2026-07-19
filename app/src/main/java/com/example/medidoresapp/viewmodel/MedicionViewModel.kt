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
 * Estas son las opciones de cómo podemos ordenar la lista de mediciones.
 */
enum class SortType {
    DATE_DESC,    // Las más nuevas arriba
    DATE_ASC,     // Las más viejitas arriba
    VALUE_DESC,   // La lectura más alta primero
    VALUE_ASC,    // La lectura más baja primero
    SERVICE_TYPE  // Agrupadas por Agua, Luz y Gas
}

/**
 * El ViewModel es el "cerebro" de la pantalla. Aquí decidimos qué mostrar
 * y cómo procesar los datos para que la interfaz no tenga que hacer mucho trabajo.
 */
class MedicionViewModel(
    private val repository: MedicionRepository
) : ViewModel() {

    // Esta es la lista que la pantalla está "vigilando" para actualizarse sola
    private val _mediciones = MutableLiveData<List<Medicion>>()
    val mediciones: LiveData<List<Medicion>> get() = _mediciones

    // Guardamos cuál es el orden actual (por defecto, las más recientes primero)
    private var currentSort = SortType.DATE_DESC
    
    // Herramienta para entender las fechas en formato día-mes-año
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    init {
        // En cuanto arranca la app, buscamos los datos
        obtenerMediciones()
    }

    /**
     * Va a buscar todas las mediciones a la base de datos y les aplica el orden elegido.
     */
    fun obtenerMediciones() {
        viewModelScope.launch {
            val lista = withContext(Dispatchers.IO) {
                repository.obtenerTodasLasMediciones()
            }
            aplicarOrden(lista)
        }
    }

    /**
     * Cambia la forma en que ordenamos la lista y la refresca.
     */
    fun setSortType(sortType: SortType) {
        currentSort = sortType
        _mediciones.value?.let { aplicarOrden(it) }
    }

    /**
     * Aquí está la magia del ordenamiento según lo que elija el usuario.
     */
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

    /**
     * Convierte el texto de la fecha en algo que la computadora pueda comparar fácilmente.
     */
    private fun parseDate(fechaStr: String): Long {
        return try {
            dateFormat.parse(fechaStr)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Decide si hay que crear una medición nueva o actualizar una que ya teníamos.
     */
    fun guardarMedicion(medicion: Medicion) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (medicion.id == 0) {
                    repository.insertarMedicion(medicion)
                } else {
                    repository.actualizarMedicion(medicion)
                }
            }
            obtenerMediciones() // Refrescamos la lista para ver el cambio
        }
    }

    /**
     * Borra la medición seleccionada.
     */
    fun eliminarMedicion(medicion: Medicion) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.eliminarMedicion(medicion)
            }
            obtenerMediciones() // Refrescamos la lista
        }
    }
}
