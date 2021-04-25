package ua.zloyhr.newsnow.ui.savedNews

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ua.zloyhr.newsnow.R
import ua.zloyhr.newsnow.databinding.FragmentSavedNewsBinding
import ua.zloyhr.newsnow.ui.NewsRecyclerAdapter

@AndroidEntryPoint
class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    private val viewModel: SavedNewsViewModel by viewModels()
    private lateinit var binding: FragmentSavedNewsBinding
    private lateinit var adapter: NewsRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedNewsBinding.bind(view)
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.savedNews.collect {
                adapter.submitList(it)
            }
        }

        adapter.setOnItemClickListener {
            val action = SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment(it)
            findNavController().navigate(action)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val article = adapter.currentList[pos]
                viewModel.delete(article)

                Snackbar.make(view,"Article deleted",Snackbar.LENGTH_LONG)
                    .setAction("UNDO"){
                        viewModel.upsert(article)
                    }.show()
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvSavedNewsList)
    }

    private fun setupRecyclerView() {
        adapter = NewsRecyclerAdapter()
        binding.rvSavedNewsList.apply {
            adapter = this@SavedNewsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}