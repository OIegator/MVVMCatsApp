package com.olegator.mvvmcatsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.olegator.mvvmcatsapp.R
import com.olegator.mvvmcatsapp.adapters.CatsAdapter
import com.olegator.mvvmcatsapp.ui.CatsActivity
import com.olegator.mvvmcatsapp.ui.CatsViewModel
import com.olegator.mvvmcatsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_download_cats.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DownloadCatsFragment : Fragment(R.layout.fragment_download_cats) {

    lateinit var viewModel: CatsViewModel
    lateinit var catsAdapter: CatsAdapter
    val TAG = "SearchCatsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as CatsActivity).viewModel
        setUpRecycleView()

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.getFavorites("user", 10)
                    }
                }
            }
        }

        viewModel.favouriteCats.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { catsResponse ->
                        catsAdapter.differ.submitList(catsResponse)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, " An error occurred: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecycleView() {
        catsAdapter = CatsAdapter()
        rvSearchNews.apply {
            adapter = catsAdapter
            layoutManager = GridLayoutManager(activity, 2)
        }
    }
}
