package com.hyperone.newsapp.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyperone.newsapp.R
import com.hyperone.newsapp.databinding.ItemVerticalNewsBinding
import com.hyperone.newsapp.home.model.ArticleEntity
import com.hyperone.newsapp.utils.extentions.onDebouncedListener
import java.util.Locale

class VerticalNewsAdapter(
    private val itemNewClickCallBack: (article: ArticleEntity) -> Unit,
    private val onFavouriteCallback: (article: ArticleEntity, isFavorite: Boolean, position: Int) -> Unit,
) : RecyclerView.Adapter<VerticalNewsAdapter.VerticalNewsViewHolder>(), Filterable {

    private var newsList: List<ArticleEntity> = ArrayList()
    private var filterSpinnerList: List<ArticleEntity> = ArrayList()


    private lateinit var binding: ItemVerticalNewsBinding

    inner class VerticalNewsViewHolder(private val binding: ItemVerticalNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(article: ArticleEntity) {

            binding.title.text = article.title
            binding.description.text = article.description
            binding.author.text = article.author
            binding.publishedDate.text = article.publishedAt

            Glide.with(binding.root.context)
                .load(article.urlToImage.toString())
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.imgNews)

            binding.btnFavorite.setImageResource(
                if (article.isFavorite) R.drawable.ic_favorite else R.drawable.ic_not_favorite
            )

            binding.root.onDebouncedListener {
                itemNewClickCallBack.invoke(article)
            }

            binding.btnFavorite.onDebouncedListener {
                onFavouriteCallback.invoke(article, article.isFavorite, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalNewsViewHolder {
        binding =
            ItemVerticalNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VerticalNewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VerticalNewsViewHolder, position: Int) {
        val article = newsList[position]

        // bind view
        holder.bindData(article)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    fun submitData(data: List<ArticleEntity>) {
        newsList = data
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString =
                    constraint?.toString()?.lowercase(Locale.getDefault()).toString().trim()
                filterSpinnerList = if (charString.isEmpty()) newsList else {
                    newsList.filter { it.title!!.lowercase().contains(charString) }
                }
                return FilterResults().apply { values = filterSpinnerList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterSpinnerList = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<ArticleEntity>
                notifyDataSetChanged()
            }
        }
    }
}