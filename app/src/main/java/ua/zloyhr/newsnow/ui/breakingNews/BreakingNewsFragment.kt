package ua.zloyhr.newsnow.ui.breakingNews

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ua.zloyhr.newsnow.R
import ua.zloyhr.newsnow.databinding.FragmentBreakingNewsBinding
import ua.zloyhr.newsnow.ui.NewsRecyclerAdapter
import ua.zloyhr.newsnow.util.Resource

@AndroidEntryPoint
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private val viewModel: BreakingNewsViewModel by viewModels()
    private lateinit var adapter: NewsRecyclerAdapter
    private lateinit var binding: FragmentBreakingNewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBreakingNewsBinding.bind(view)
        setupRecyclerView()
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.breakingNews.collect {
                when(it){
                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { newsResponse ->
                            adapter.submitList(newsResponse.articles.toList())
                            val totalPages = newsResponse.totalResults / 20 + 2
                            isLastPage = totalPages == viewModel.currentPage
                            if(isLastPage){
                                binding.rvBreakingNewsList.setPadding(0,0,0,0)
                            }else{
                                binding.rvBreakingNewsList.setPadding(0,0,0,50)
                            }
                        }

                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        it.message?.let{ message ->
                            Log.e("Breaking News","Error $message")
                            Toast.makeText(requireContext(),"An error: $message",Toast.LENGTH_LONG).show()
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            }
        }

        adapter.setOnItemClickListener {
            val action = BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isScrolling = false
    var isLastPage = false


    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)isScrolling = true
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val lastVisible = layoutManager.findLastVisibleItemPosition()
            val firstVisible = layoutManager.findFirstVisibleItemPosition()
            val visibleSize = layoutManager.childCount
            val size = layoutManager.itemCount

            val isAtLastItem = firstVisible + visibleSize >= size
            val isNotAtBeginning = firstVisible >= 0
            val isTotalMoreThanVisible = size >= 20//TODO
            val shouldPaginate = !isLoading && !isLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
            if(shouldPaginate){
                viewModel.getBreakingNews("ua")
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView(){
        adapter = NewsRecyclerAdapter()
        binding.rvBreakingNewsList.apply {
            adapter = this@BreakingNewsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}