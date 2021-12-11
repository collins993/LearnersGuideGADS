package io.github.collins993.learnersguide.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.collins993.mvvmtraining.databinding.ItemArticlePreviewBinding
import io.github.collins993.mvvmtraining.models.Article

class CourseAdapter() : RecyclerView.Adapter<CourseAdapter.ArticleViewHolder>() {


    inner class ArticleViewHolder(val binding: ItemArticlePreviewBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.binding.tvSource.text = article.source?.name
        holder.binding.tvTitle.text = article.title
        holder.binding.tvDescription.text = article.description
        holder.binding.tvPublishedAt.text = article.publishedAt

        Glide.with(holder.itemView.context)
            .load(article.urlToImage)
            .into(holder.binding.ivArticleImage)

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(article) }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null


    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }


}