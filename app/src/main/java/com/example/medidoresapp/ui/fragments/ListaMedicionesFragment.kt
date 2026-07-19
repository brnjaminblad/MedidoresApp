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
 * Fragmento que muestra el listado de mediciones con opciones de editar, eliminar y ordenar.
 */
class ListaMedicionesFragment : Fragment() {

    private var _binding: FragmentListaMedicionesBinding? = null
    private val binding get() = _binding!!

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

        configurarMenu()
        configurarRecyclerView()
        observarDatos()

        binding.btnNueva.setOnClickListener {
            val bundle = Bundle().apply { putInt("medicionId", 0) }
            findNavController().navigate(R.id.action_listaMedicionesFragment_to_registroMedicionFragment, bundle)
        }
    }

    /**
     * Configura el menú de la Toolbar para añadir la opción de ordenar.
     */
    private fun configurarMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_lista, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sort -> {
                        viewModel.alternarOrden()
                        Toast.makeText(requireContext(), "Orden de fechas cambiado", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun configurarRecyclerView() {
        adapter = MedicionAdapter(
            onEdit = { medicion ->
                val bundle = Bundle().apply { putInt("medicionId", medicion.id) }
                findNavController().navigate(R.id.action_listaMedicionesFragment_to_registroMedicionFragment, bundle)
            },
            onDelete = { medicion ->
                confirmarEliminacion(medicion)
            }
        )
        binding.rvMediciones.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ListaMedicionesFragment.adapter
        }
    }

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

    private fun observarDatos() {
        viewModel.mediciones.observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.obtenerMediciones()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
