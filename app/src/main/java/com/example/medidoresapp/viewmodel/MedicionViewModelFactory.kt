package com.example.medidoresapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medidoresapp.data.repository.MedicionRepository

class MedicionViewModelFactory(
    private val repository: MedicionRepository
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(MedicionViewModel::class.java)) {

            return MedicionViewModel(repository) as T

        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )

    }
}