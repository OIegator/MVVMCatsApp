package com.olegator.mvvmcatsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.olegator.mvvmcatsapp.repository.CatsRepository

class CatsViewModelProviderFactory(
    val catsRepository: CatsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CatsViewModel(catsRepository) as T
    }
}