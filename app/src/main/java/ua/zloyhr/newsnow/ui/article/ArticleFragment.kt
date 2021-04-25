package ua.zloyhr.newsnow.ui.article

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ua.zloyhr.newsnow.R
import ua.zloyhr.newsnow.databinding.FragmentArticleBinding
import ua.zloyhr.newsnow.databinding.FragmentBreakingNewsBinding
import ua.zloyhr.newsnow.ui.NewsRecyclerAdapter
import ua.zloyhr.newsnow.ui.breakingNews.BreakingNewsViewModel

@AndroidEntryPoint
class ArticleFragment : Fragment(R.layout.fragment_article) {
    private val viewModel: ArticleViewModel by viewModels()
    private lateinit var binding: FragmentArticleBinding
    private val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        val article = args.article

        binding.apply {
            wvArticle.apply {
                webViewClient = WebViewClient()
                article.url?.let{loadUrl(it)}
            }

            fabSaveArticle.setOnClickListener {
                viewModel.upsert(article)
                Snackbar.make(view,"Article saved successfully",Snackbar.LENGTH_LONG).show()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                wvArticle.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    if(scrollY > 500){
                        fabSaveArticle.hide()
                    }else{
                        fabSaveArticle.show()
                    }
                }
            }
        }
    }
}