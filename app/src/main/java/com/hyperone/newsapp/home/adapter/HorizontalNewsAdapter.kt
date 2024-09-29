package com.hyperone.newsapp.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyperone.newsapp.R
import com.hyperone.newsapp.databinding.ItemHorizontalNewsBinding
import com.hyperone.newsapp.home.model.BreakingNewsEntity

class HorizontalNewsAdapter :
    RecyclerView.Adapter<HorizontalNewsAdapter.HorizontalNewsViewHolder>() {

    private var newsList: List<BreakingNewsEntity> = ArrayList()

    private lateinit var binding: ItemHorizontalNewsBinding

    inner class HorizontalNewsViewHolder(private val binding: ItemHorizontalNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(article: BreakingNewsEntity) {

            binding.title.text = article.title

            Glide.with(binding.root.context)
                .load(article.urlToImage)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.imgNews)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalNewsViewHolder {
        binding =
            ItemHorizontalNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HorizontalNewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HorizontalNewsViewHolder, position: Int) {
        val article = newsList[position]

        // bind view
        holder.bindData(article)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    fun submitData(data: List<BreakingNewsEntity>) {
        newsList = data
        notifyDataSetChanged()
    }
}