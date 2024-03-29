package com.example.simplemvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplemvvmnewsapp.R
import com.example.simplemvvmnewsapp.data.models.Article
import com.example.simplemvvmnewsapp.util.Constants.Companion.DEFAULT_IMAGE
import kotlinx.android.synthetic.main.item_article_review.view.*
import android.util.TypedValue




class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>(){

    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private var textSize = 22F

    private val differCallback = object: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_review,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.itemView.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize )

        holder.itemView.apply {
            if (article.urlToImage.isNullOrEmpty()){
                Glide.with(this).load(DEFAULT_IMAGE).into(ivArticleImage)
            }else{
                Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            }
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            setOnClickListener{
                onItemClickListener?.let { it(article) }
            }
        }
    }

    fun setTextSize(textSize: Float){
        this.textSize = textSize
        notifyDataSetChanged()
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }
}