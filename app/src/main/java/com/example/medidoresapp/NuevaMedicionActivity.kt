package com.example.medidoresapp

import android.app.DatePickerDialog
import android.widget.Toast
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.medidoresapp.data.database.AppDatabase
import com.example.medidoresapp.data.entity.Medicion
import com.example.medidoresapp.data.repository.MedicionRepository
import com.example.medidoresapp.databinding.ActivityNuevaMedicionBinding
import com.example.medidoresapp.viewmodel.MedicionViewModel
import com.example.medidoresapp.viewmodel.MedicionViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class NuevaMedicionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNuevaMedicionBinding

    private var tipoServicioSeleccionado = ""


    private val viewModel: MedicionViewModel by viewModels {

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "medidores_database"
        ).build()


        val repository = MedicionRepository(
            database.medicionDao()
        )


        MedicionViewModelFactory(repository)

    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        binding = ActivityNuevaMedicionBinding.inflate(layoutInflater)

        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }


        configurarEventos()
        configurarFecha()

    }


    private fun configurarEventos() {


        // -----------------------------
        // TOGGLE AGUA / LUZ / GAS
        // -----------------------------

        binding.toggleTipoServicio.addOnButtonCheckedListener { _, checkedId, isChecked ->


            if (isChecked) {


                tipoServicioSeleccionado = when (checkedId) {


                    binding.btnAgua.id -> "Agua"


                    binding.btnLuz.id -> "Luz"


                    binding.btnGas.id -> "Gas"


                    else -> ""

                }

            }

        }


        // -----------------------------
        // CALENDARIO FECHA
        // -----------------------------

        binding.etFecha.setOnClickListener {


            val calendario =
                Calendar.getInstance()


            val año =
                calendario.get(Calendar.YEAR)


            val mes =
                calendario.get(Calendar.MONTH)


            val dia =
                calendario.get(Calendar.DAY_OF_MONTH)



            DatePickerDialog(

                this,

                { _, year, month, day ->


                    binding.etFecha.setText(

                        "$day/${month + 1}/$year"

                    )


                },

                año,
                mes,
                dia

            ).show()

        }


        // -----------------------------
        // GUARDAR
        // -----------------------------

        binding.btnGuardar.setOnClickListener {


            val lectura =
                binding.etLectura.text.toString()
                    .toDoubleOrNull()


            val fecha =
                binding.etFecha.text.toString()


            // Validar servicio

            if (tipoServicioSeleccionado.isEmpty()) {


                Toast.makeText(

                    this,

                    "Seleccione Agua, Luz o Gas",

                    Toast.LENGTH_SHORT

                ).show()


                return@setOnClickListener

            }


            // Validar lectura

            if (lectura == null) {


                binding.etLectura.error =
                    "Ingrese una lectura válida"


                binding.etLectura.requestFocus()


                return@setOnClickListener

            }


            // Validar fecha

            if (fecha.isEmpty()) {


                binding.etFecha.error =
                    "Seleccione una fecha"


                binding.etFecha.requestFocus()


                return@setOnClickListener

            }


            val medicion = Medicion(


                tipoServicio = tipoServicioSeleccionado,


                lectura = lectura,


                fecha = fecha

            )



            viewModel.guardarMedicion(medicion)



            Toast.makeText(

                this,

                "Medición guardada correctamente",

                Toast.LENGTH_SHORT

            ).show()



            finish()

        }


        // -----------------------------
        // CANCELAR
        // -----------------------------

        binding.btnCancelar.setOnClickListener {


            finish()

        }

    }

    private fun configurarFecha() {

        binding.etFecha.setOnClickListener {

            val calendario = Calendar.getInstance()

            val dia = calendario.get(Calendar.DAY_OF_MONTH)
            val mes = calendario.get(Calendar.MONTH)
            val anio = calendario.get(Calendar.YEAR)


            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->

                    val fecha =
                        String.format(
                            "%02d/%02d/%04d",
                            dayOfMonth,
                            month + 1,
                            year
                        )

                    binding.etFecha.setText(fecha)

                },
                anio,
                mes,
                dia
            )


            datePicker.show()

        }

    }
}