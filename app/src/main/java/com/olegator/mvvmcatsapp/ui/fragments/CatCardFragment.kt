package com.olegator.mvvmcatsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.olegator.mvvmcatsapp.R
import com.olegator.mvvmcatsapp.ui.CatsActivity
import com.olegator.mvvmcatsapp.ui.CatsViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_caticle.*


class CatCardFragment : Fragment(R.layout.fragment_caticle) {

    lateinit var viewModel: CatsViewModel
    val args: CatCardFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as CatsActivity).viewModel
        val cat = args.cat
        Picasso.get()
            .load(cat.url)
            .fit()
            .centerCrop()
            .into(iv_cat)
        fab.setOnClickListener {
            viewModel.saveCat(cat)
            Snackbar.make(view, "Cat saved successfully", Snackbar.LENGTH_SHORT).show()
        }
        btn_like.setOnClickListener {
            viewModel.likeCat(cat)
            Snackbar.make(view, "Cat liked successfully", Snackbar.LENGTH_SHORT).show()
        }
        btn_dislike.setOnClickListener {
            viewModel.nopeCat(cat)
            Snackbar.make(view, "Cat disliked successfully", Snackbar.LENGTH_SHORT).show()
        }

    }
}