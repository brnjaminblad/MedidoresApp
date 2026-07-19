package com.example.medidoresapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medidoresapp.R
import com.example.medidoresapp.adapter.MedicionAdapter
import com.example.medidoresapp.data.database.AppDatabase
import com.example.medidoresapp.data.repository.MedicionRepository
import com.example.medidoresapp.databinding.FragmentListaMedicionesBinding
import com.example.medidoresapp.viewmodel.MedicionViewModel
import com.example.medidoresapp.viewmodel.MedicionViewModelFactory

/**
 * Esta pantalla muestra la lista de todas las mediciones que hemos registrado.
 */
class ListaMedicionesFragment : Fragment() {

    private var _binding: FragmentListaMedicionesBinding? = null
    private val binding get() = _binding!!

    // Inicializamos el ViewModel con su fábrica para que tenga acceso al repositorio
    private val viewModel: MedicionViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = MedicionRepository(database.medicionDao())
        MedicionViewModelFactory(repository)
    }

    private lateinit var adapter: MedicionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaMedicionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarMenu()        // Preparamos el botón de ordenar
        configurarRecyclerView() // Preparamos la lista visual
        observarDatos()         // Nos ponemos a escuchar cambios en los datos

        // Configuramos el botón flotante (+) para ir a registrar una nueva
        binding.btnNueva.setOnClickListener {
            val bundle = Bundle().apply { putInt("medicionId", 0) }
            findNavController().navigate(R.id.action_listaMedicionesFragment_to_registroMedicionFragment, bundle)
        }
    }

    /**
     * Aquí configuramos el menú de la parte superior (el icono de ordenar).
     */
    private fun configurarMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Inflamos el archivo XML que contiene las opciones de ordenamiento
                menuInflater.inflate(R.menu.menu_lista, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Según lo que el usuario toque en el submenú, avisamos al ViewModel
                return when (menuItem.itemId) {
                    R.id.sort_date_desc -> {
                        viewModel.setSortType(com.example.medidoresapp.viewmodel.SortType.DATE_DESC)
                        true
                    }
                    R.id.sort_date_asc -> {
                        viewModel.setSortType(com.example.medidoresapp.viewmodel.SortType.DATE_ASC)
                        true
                    }
                    R.id.sort_value_desc -> {
                        viewModel.setSortType(com.example.medidoresapp.viewmodel.SortType.VALUE_DESC)
                        true
                    }
                    R.id.sort_value_asc -> {
                        viewModel.setSortType(com.example.medidoresapp.viewmodel.SortType.VALUE_ASC)
                        true
                    }
                    R.id.sort_type -> {
                        viewModel.setSortType(com.example.medidoresapp.viewmodel.SortType.SERVICE_TYPE)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * Configuramos cómo se va a ver la lista y qué pasa si tocamos un ítem.
     */
    private fun configurarRecyclerView() {
        adapter = MedicionAdapter(
            onEdit = { medicion ->
                // Si tocamos editar, vamos a la pantalla de registro pasando el ID
                val bundle = Bundle().apply { putInt("medicionId", medicion.id) }
                findNavController().navigate(R.id.action_listaMedicionesFragment_to_registroMedicionFragment, bundle)
            },
            onDelete = { medicion ->
                // Si tocamos eliminar, mostramos un aviso de confirmación
                confirmarEliminacion(medicion)
            }
        )
        binding.rvMediciones.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ListaMedicionesFragment.adapter
        }
    }

    /**
     * Muestra un cartelito para asegurar que el usuario realmente quiere borrar el dato.
     */
    private fun confirmarEliminacion(medicion: com.example.medidoresapp.data.entity.Medicion) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.opcion_eliminar)
            .setMessage(R.string.msg_confirmar_eliminar)
            .setPositiveButton(R.string.opcion_eliminar) { _, _ ->
                viewModel.eliminarMedicion(medicion)
                Toast.makeText(requireContext(), "Registro eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    /**
     * Este método se encarga de que la lista se actualice solita cuando cambian los datos.
     */
    private fun observarDatos() {
        viewModel.mediciones.observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)
        }
    }

    override fun onResume() {
        super.onResume()
        // Cada vez que volvemos a esta pantalla, refrescamos los datos por si acaso
        viewModel.obtenerMediciones()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
