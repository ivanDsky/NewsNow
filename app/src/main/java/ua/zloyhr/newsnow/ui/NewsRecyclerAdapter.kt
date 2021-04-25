package ua.zloyhr.newsnow.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ua.zloyhr.newsnow.R
import ua.zloyhr.newsnow.data.api.Article
import ua.zloyhr.newsnow.databinding.ItemNewsBinding

class NewsRecyclerAdapter: ListAdapter<Article,NewsRecyclerAdapter.NewsViewHolder>(ArticleDiff) {

    inner class NewsViewHolder(val binding: ItemNewsBinding): RecyclerView.ViewHolder(binding.root){

    }

    object ArticleDiff : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem.hashCode() == newItem.hashCode()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = getItem(position)
        holder.binding.apply {
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvSource.text = article.source.name
            tvDatePublished.text = article.publishedAt
            Glide.with(ivArticleImage).load(article.urlToImage).into(ivArticleImage)
            ivArticleImage.contentDescription = article.title
            itemNews.setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Article) -> Unit)){
        onItemClickListener = listener
    }
}