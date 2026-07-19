package com.example.medidoresapp
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.medidoresapp.adapter.MedicionAdapter
import com.example.medidoresapp.data.database.AppDatabase
import com.example.medidoresapp.data.repository.MedicionRepository
import com.example.medidoresapp.databinding.ActivityMainBinding
import com.example.medidoresapp.viewmodel.MedicionViewModel
import com.example.medidoresapp.viewmodel.MedicionViewModelFactory


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding


    private lateinit var adapter: MedicionAdapter



    private val viewModel: MedicionViewModel by viewModels {


        val database =
            Room.databaseBuilder(

                applicationContext,

                AppDatabase::class.java,

                "medidores_database"

            ).build()



        val repository =
            MedicionRepository(

                database.medicionDao()

            )



        MedicionViewModelFactory(repository)


    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        enableEdgeToEdge()



        binding =
            ActivityMainBinding.inflate(layoutInflater)


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



        configurarRecyclerView()


        observarDatos()



        binding.btnNueva.setOnClickListener {


            startActivity(

                Intent(

                    this,

                    NuevaMedicionActivity::class.java

                )

            )

        }


    }



    private fun configurarRecyclerView(){


        adapter =
            MedicionAdapter()



        binding.rvMediciones.layoutManager =
            LinearLayoutManager(this)



        binding.rvMediciones.adapter =
            adapter


    }



    private fun observarDatos(){

        viewModel.obtenerMediciones { lista ->

            adapter.actualizarLista(lista)

        }

    }


}