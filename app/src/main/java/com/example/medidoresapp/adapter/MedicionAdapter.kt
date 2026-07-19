package com.example.medidoresapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medidoresapp.data.entity.Medicion
import com.example.medidoresapp.databinding.ItemMedicionBinding


class MedicionAdapter :

    RecyclerView.Adapter<MedicionAdapter.MedicionViewHolder>() {


    private var listaMediciones =
        emptyList<Medicion>()



    fun actualizarLista(
        nuevaLista: List<Medicion>
    ){

        listaMediciones = nuevaLista

        notifyDataSetChanged()

    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicionViewHolder {


        val binding =
            ItemMedicionBinding.inflate(

                LayoutInflater.from(parent.context),

                parent,

                false

            )


        return MedicionViewHolder(binding)

    }



    override fun onBindViewHolder(
        holder: MedicionViewHolder,
        position: Int
    ) {


        val medicion =
            listaMediciones[position]


        holder.bind(medicion)

    }



    override fun getItemCount(): Int {

        return listaMediciones.size

    }



    class MedicionViewHolder(
        private val binding: ItemMedicionBinding
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(
            medicion: Medicion
        ){

            binding.txtServicio.text =
                medicion.tipoServicio


            binding.txtLectura.text =
                "Lectura: ${medicion.lectura}"


            binding.txtFecha.text =
                "Fecha: ${medicion.fecha}"

        }

    }

}