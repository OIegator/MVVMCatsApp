package com.olegator.mvvmcatsapp.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.olegator.mvvmcatsapp.ui.CatsViewModel
import com.olegator.mvvmcatsapp.R
import com.olegator.mvvmcatsapp.adapters.CatsAdapter
import com.olegator.mvvmcatsapp.databinding.FragmentHomeCatsBinding
import com.olegator.mvvmcatsapp.ui.CatsActivity
import com.olegator.mvvmcatsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.olegator.mvvmcatsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_home_cats.*

class HomeCatsFragment : Fragment(R.layout.fragment_home_cats) {

    lateinit var viewModel: CatsViewModel
    lateinit var catsAdapter: CatsAdapter
    private var _binding: FragmentHomeCatsBinding? = null
    private val binding get() = _binding!!

    val TAG = "HomeCatsFragment"

    override fun onResume() {
        super.onResume()
        val breeds = resources.getStringArray(R.array.breeds)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, breeds)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Toast.makeText(requireContext(),
                    breeds[position],
                    Toast.LENGTH_SHORT).show();
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeCatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as CatsActivity).viewModel
        setUpRecycleView()

        autoCompleteTextView.inputType = InputType.TYPE_NULL

        catsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("cat", it)
            }
            findNavController().navigate(
                R.id.action_homeCatsFragment_to_catCardFragment,
                bundle
            )
        }

        viewModel.catsImages.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { catsResponse ->
                        catsAdapter.differ.submitList(catsResponse)
                        if(isLastPage) {
                            rvHomeCats.setPadding(0, 0, 0, 0)
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getImages(10)
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
        rvHomeCats.apply {
            adapter = catsAdapter
            layoutManager = GridLayoutManager(activity, 2)
            addOnScrollListener(this@HomeCatsFragment.scrollListener)
        }
    }


}