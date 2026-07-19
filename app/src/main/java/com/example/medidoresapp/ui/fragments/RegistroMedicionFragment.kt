package com.example.medidoresapp.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.medidoresapp.R
import com.example.medidoresapp.data.database.AppDatabase
import com.example.medidoresapp.data.entity.Medicion
import com.example.medidoresapp.data.repository.MedicionRepository
import com.example.medidoresapp.databinding.FragmentRegistroMedicionBinding
import com.example.medidoresapp.viewmodel.MedicionViewModel
import com.example.medidoresapp.viewmodel.MedicionViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Esta pantalla sirve tanto para anotar una nueva medición como para corregir una vieja.
 */
class RegistroMedicionFragment : Fragment() {

    private var _binding: FragmentRegistroMedicionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MedicionViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = MedicionRepository(database.medicionDao())
        MedicionViewModelFactory(repository)
    }

    // Si este ID es 0, es una nueva. Si no, estamos editando una existente.
    private var medicionId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroMedicionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Miramos si nos pasaron un ID para editar
        medicionId = arguments?.getInt("medicionId") ?: 0

        if (medicionId != 0) {
            // Si hay ID, buscamos los datos viejos para rellenar los campos
            cargarDatosMedicion(medicionId)
            binding.btnGuardar.text = getString(R.string.btn_actualizar)
        }

        // Al tocar el campo de fecha, abrimos el calendario
        binding.etFecha.setOnClickListener {
            mostrarDatePicker()
        }

        // Al tocar guardar, validamos y mandamos los datos al ViewModel
        binding.btnGuardar.setOnClickListener {
            guardarMedicion()
        }
    }

    /**
     * Busca los datos de una medición guardada y los pone en los campos de texto.
     */
    private fun cargarDatosMedicion(id: Int) {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val medicion = withContext(Dispatchers.IO) {
                database.medicionDao().obtenerMedicionPorId(id)
            }
            medicion?.let {
                binding.etLectura.setText(it.lectura.toString())
                binding.etFecha.setText(it.fecha)
                // Marcamos el botón de Agua, Luz o Gas según corresponda
                when (it.tipoServicio) {
                    "Agua" -> binding.toggleGroupServicio.check(R.id.btnAgua)
                    "Luz" -> binding.toggleGroupServicio.check(R.id.btnLuz)
                    "Gas" -> binding.toggleGroupServicio.check(R.id.btnGas)
                }
            }
        }
    }

    /**
     * Muestra el calendario de Android para elegir una fecha cómodamente.
     */
    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Guardamos la fecha con formato lindo: DD-MM-YYYY
                val fechaFormatted = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etFecha.setText(fechaFormatted)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    /**
     * Revisa que todo esté bien escrito antes de guardar.
     */
    private fun guardarMedicion() {
        val lecturaStr = binding.etLectura.text.toString()
        val fecha = binding.etFecha.text.toString()
        val checkedButtonId = binding.toggleGroupServicio.checkedButtonId

        // Validaciones básicas para no guardar datos vacíos
        if (checkedButtonId == View.NO_ID) {
            Toast.makeText(requireContext(), "Por favor, selecciona qué medidor es", Toast.LENGTH_SHORT).show()
            return
        }

        if (lecturaStr.isEmpty()) {
            binding.tilLectura.error = "Escribe el valor del medidor"
            return
        }
        binding.tilLectura.error = null

        if (fecha.isEmpty()) {
            binding.tilFecha.error = "Selecciona una fecha"
            return
        }
        binding.tilFecha.error = null

        // Identificamos el tipo de servicio seleccionado
        val tipoServicio = when (checkedButtonId) {
            R.id.btnAgua -> "Agua"
            R.id.btnLuz -> "Luz"
            R.id.btnGas -> "Gas"
            else -> "Agua"
        }

        // Creamos el objeto con la información
        val medicion = Medicion(
            id = medicionId,
            tipoServicio = tipoServicio,
            lectura = lecturaStr.toDouble(),
            fecha = fecha
        )

        // Le pedimos al ViewModel que lo guarde y volvemos atrás
        viewModel.guardarMedicion(medicion)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
