package com.olegator.mvvmcatsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import api.services.ImagesService
import com.olegator.mvvmcatsapp.R
import com.olegator.mvvmcatsapp.di.CatsApplication
import com.olegator.mvvmcatsapp.repository.CatsRepository

import kotlinx.android.synthetic.main.activity_cats.*
import javax.inject.Inject

class CatsActivity : AppCompatActivity() {

    @Inject
    lateinit var service: ImagesService
    lateinit var viewModel: CatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cats)
        (application as CatsApplication).applicationComponent.inject(this)

        val repository = CatsRepository(service)

        viewModel = ViewModelProvider(
            this,
            CatsViewModelProviderFactory(repository)
        ).get(CatsViewModel::class.java)
        bottomNavigationView.setupWithNavController(catsNavHostFragment.findNavController())
    }
}
