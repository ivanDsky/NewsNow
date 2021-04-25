package ua.zloyhr.newsnow.ui.searchNews

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import ua.zloyhr.newsnow.R
import ua.zloyhr.newsnow.databinding.FragmentSavedNewsBinding
import ua.zloyhr.newsnow.databinding.FragmentSearchNewsBinding
import ua.zloyhr.newsnow.ui.NewsRecyclerAdapter
import ua.zloyhr.newsnow.ui.breakingNews.BreakingNewsFragmentDirections
import ua.zloyhr.newsnow.util.Resource

@AndroidEntryPoint
class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    private val viewModel: SearchNewsViewModel by viewModels()
    private lateinit var binding: FragmentSearchNewsBinding
    private lateinit var adapter: NewsRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchNewsBinding.bind(view)
        setupRecyclerAdapter()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.searchNews.collect {
                when (it) {
                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { newsResponse ->
                            adapter.submitList(newsResponse.articles)
                            val totalPages = newsResponse.totalResults / 20 + 2
                            isLastPage = totalPages == viewModel.currentPage
                        }
                        if (isLastPage) {
                            binding.rvSearchNewsList.setPadding(0, 0, 0, 0)
                        } else {
                            binding.rvSearchNewsList.setPadding(0, 0, 0, 50)
                        }

                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        it.message?.let { message ->
                            if(message == "Empty search bar")return@let
                            Log.e("Search News", "Error $message")
                            Toast.makeText(requireContext(),"An error: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            }
        }

        var searchJob: Job? = null

        binding.svSearchNews.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    delay(500L)
                    newText?.let {search ->
                        binding.rvSearchNewsList.smoothScrollToPosition(0)
                        viewModel.resetSearch()
                        if (search.isNotBlank()) viewModel.searchNews(search)
                    }
                }
                return true
            }
        })

        adapter.setOnItemClickListener {
            val action = SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun hideProgressBar() {
        binding.searchProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.searchProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isScrolling = false
    var isLastPage = false


    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) isScrolling =
                true
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisible = layoutManager.findFirstVisibleItemPosition()
            val visibleSize = layoutManager.childCount
            val size = layoutManager.itemCount

            val isAtLastItem = firstVisible + visibleSize >= size
            val isNotAtBeginning = firstVisible >= 0
            val isTotalMoreThanVisible = size >= 20//TODO
            val shouldPaginate =
                !isLoading && !isLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
            if (shouldPaginate) {
                if (binding.svSearchNews.query.isNotBlank()) {
                    viewModel.searchNews(binding.svSearchNews.query.toString())
                }
                isScrolling = false
            }
        }
    }


    private fun setupRecyclerAdapter() {
        adapter = NewsRecyclerAdapter()
        binding.rvSearchNewsList.apply {
            adapter = this@SearchNewsFragment.adapter
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}