package com.example.medidoresapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medidoresapp.R
import com.example.medidoresapp.data.entity.Medicion
import com.example.medidoresapp.databinding.ItemMedicionBinding

/**
 * Adaptador para el RecyclerView que muestra el listado de mediciones con iconos mejorados.
 */
class MedicionAdapter(
    private val onEdit: (Medicion) -> Unit,
    private val onDelete: (Medicion) -> Unit
) : RecyclerView.Adapter<MedicionAdapter.MedicionViewHolder>() {

    private var listaMediciones = emptyList<Medicion>()
    private var expandedPosition: Int = -1

    fun actualizarLista(nuevaLista: List<Medicion>) {
        listaMediciones = nuevaLista
        expandedPosition = -1
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicionViewHolder {
        val binding = ItemMedicionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MedicionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicionViewHolder, position: Int) {
        val medicion = listaMediciones[position]
        val isExpanded = position == expandedPosition
        
        holder.bind(medicion, isExpanded, onEdit, onDelete) {
            val previousExpandedPosition = expandedPosition
            expandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(expandedPosition)
        }
    }

    override fun getItemCount(): Int = listaMediciones.size

    class MedicionViewHolder(private val binding: ItemMedicionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            medicion: Medicion,
            isExpanded: Boolean,
            onEdit: (Medicion) -> Unit,
            onDelete: (Medicion) -> Unit,
            onItemClick: () -> Unit
        ) {
            val context = binding.root.context
            
            val (nombreTraducido, iconoRes) = when (medicion.tipoServicio.lowercase()) {
                "agua" -> context.getString(R.string.opcion_agua) to R.drawable.ic_water
                "luz" -> context.getString(R.string.opcion_luz) to R.drawable.ic_light
                "gas" -> context.getString(R.string.opcion_gas) to R.drawable.ic_gas
                else -> medicion.tipoServicio to R.drawable.ic_water
            }

            binding.txtServicio.text = nombreTraducido
            binding.txtLectura.text = context.getString(R.string.txt_lectura_item, medicion.lectura.toString())
            binding.txtFecha.text = context.getString(R.string.txt_fecha_item, medicion.fecha)
            
            // Aplicamos el icono mejorado
            binding.imgIcono.setImageResource(iconoRes)
            
            binding.layoutActions.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.root.setOnClickListener { onItemClick() }
            binding.btnEdit.setOnClickListener { onEdit(medicion) }
            binding.btnDelete.setOnClickListener { onDelete(medicion) }
        }
    }
}
