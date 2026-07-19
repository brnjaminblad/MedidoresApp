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
 * Fragmento para registrar o editar una medición con formato de fecha DD-MM-YYYY.
 */
class RegistroMedicionFragment : Fragment() {

    private var _binding: FragmentRegistroMedicionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MedicionViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = MedicionRepository(database.medicionDao())
        MedicionViewModelFactory(repository)
    }

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

        medicionId = arguments?.getInt("medicionId") ?: 0

        if (medicionId != 0) {
            cargarDatosMedicion(medicionId)
            binding.btnGuardar.text = getString(R.string.btn_actualizar)
        }

        binding.etFecha.setOnClickListener {
            mostrarDatePicker()
        }

        binding.btnGuardar.setOnClickListener {
            guardarMedicion()
        }
    }

    private fun cargarDatosMedicion(id: Int) {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val medicion = withContext(Dispatchers.IO) {
                database.medicionDao().obtenerMedicionPorId(id)
            }
            medicion?.let {
                binding.etLectura.setText(it.lectura.toString())
                binding.etFecha.setText(it.fecha)
                when (it.tipoServicio) {
                    "Agua" -> binding.toggleGroupServicio.check(R.id.btnAgua)
                    "Luz" -> binding.toggleGroupServicio.check(R.id.btnLuz)
                    "Gas" -> binding.toggleGroupServicio.check(R.id.btnGas)
                }
            }
        }
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Formato DD-MM-YYYY con ceros a la izquierda
                val fechaFormatted = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etFecha.setText(fechaFormatted)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun guardarMedicion() {
        val lecturaStr = binding.etLectura.text.toString()
        val fecha = binding.etFecha.text.toString()
        val checkedButtonId = binding.toggleGroupServicio.checkedButtonId

        if (checkedButtonId == View.NO_ID) {
            Toast.makeText(requireContext(), "Seleccione Agua, Luz o Gas", Toast.LENGTH_SHORT).show()
            return
        }

        if (lecturaStr.isEmpty()) {
            binding.tilLectura.error = getString(R.string.error_campo_obligatorio)
            return
        }
        binding.tilLectura.error = null

        if (fecha.isEmpty()) {
            binding.tilFecha.error = getString(R.string.error_campo_obligatorio)
            return
        }
        binding.tilFecha.error = null

        val tipoServicio = when (checkedButtonId) {
            R.id.btnAgua -> "Agua"
            R.id.btnLuz -> "Luz"
            R.id.btnGas -> "Gas"
            else -> "Agua"
        }

        val medicion = Medicion(
            id = medicionId,
            tipoServicio = tipoServicio,
            lectura = lecturaStr.toDouble(),
            fecha = fecha
        )

        viewModel.guardarMedicion(medicion)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
