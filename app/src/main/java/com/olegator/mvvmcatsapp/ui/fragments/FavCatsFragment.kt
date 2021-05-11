package com.olegator.mvvmcatsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.olegator.mvvmcatsapp.R
import com.olegator.mvvmcatsapp.adapters.CatsAdapter
import com.olegator.mvvmcatsapp.ui.CatsActivity
import com.olegator.mvvmcatsapp.ui.CatsViewModel
import com.olegator.mvvmcatsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_fav_cats.*
import kotlinx.android.synthetic.main.fragment_fav_cats.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_home_cats.*


class FavCatsFragment : Fragment(R.layout.fragment_fav_cats) {

    lateinit var viewModel: CatsViewModel
    lateinit var catsAdapter: CatsAdapter
    private val TAG = "FavoriteCatsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as CatsActivity).viewModel
        setUpRecycleView()

        catsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("cat", it)
            }
            findNavController().navigate(
                R.id.action_favCatsFragment_to_catCardFragment,
                bundle
            )
        }

        viewModel.favouriteCats.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { catsResponse ->
                        catsAdapter.differ.submitList(catsResponse)
                        if(isLastPage) {
                            rvFavoritesCats.setPadding(0, 0, 0, 0)
                        }
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
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as GridLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= 10
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getFavorites("user", 10)
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setUpRecycleView() {
        catsAdapter = CatsAdapter()
        rvFavoritesCats.apply {
            adapter = catsAdapter
            layoutManager = GridLayoutManager(activity, 2)
            addOnScrollListener(this@FavCatsFragment.scrollListener)
        }
    }
}
