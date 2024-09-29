package com.hyperone.newsapp.favorite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hyperone.newsapp.R
import com.hyperone.newsapp.base.BaseFragment
import com.hyperone.newsapp.databinding.FragmentFavoriteBinding
import com.hyperone.newsapp.favorite.model.FavoriteArticleEntity
import com.hyperone.newsapp.favorite.viewModel.FavoriteViewModel
import com.hyperone.newsapp.home.adapter.VerticalNewsAdapter
import com.hyperone.newsapp.home.model.ArticleEntity
import com.hyperone.newsapp.network.DataHandler
import com.hyperone.newsapp.utils.extentions.customNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : BaseFragment() {

    private var _binding: FragmentFavoriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels()

    private lateinit var verticalNewsAdapter: VerticalNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        subscribeData()
    }

    private fun initRecyclerView() {
        binding.rvFavoriteNews.apply {
            verticalNewsAdapter = VerticalNewsAdapter(
                itemNewClickCallBack = { article ->
                    val bundle = Bundle()
                    bundle.putString("Article_url", article.urlToImage)
                    findNavController().customNavigate(R.id.newsDetailsFragment, data = bundle)
                },
                onFavouriteCallback = { article, isFavorite, _ ->
                    if (isFavorite) {
                        viewModel.removeArticle(
                            FavoriteArticleEntity(
                                id = article.id,
                                title = article.title,
                                description = article.description,
                                urlToImage = article.urlToImage.toString(),
                                author = article.author,
                                url = article.url,
                                publishedAt = article.publishedAt
                            )
                        )
                    } else {
                        viewModel.saveArticle(article)
                    }
                    article.isFavorite = !article.isFavorite // Toggle favorite status
                },
            )
            adapter = verticalNewsAdapter
        }
    }

    private fun subscribeData() {
        viewModel.favoriteArticles.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DataHandler.ShowLoading -> {
                    // Show loading
                    showLoading()
                }

                is DataHandler.HideLoading -> {
                    // Hide loading
                    hideLoading()
                }

                is DataHandler.Success -> {
                    // Update UI with the list of favorite articles
                    verticalNewsAdapter.submitData(result.data.map { it.toAllNewsEntity() }) // This is now List<FavoriteArticleEntity>
                }

                is DataHandler.Error -> {
                    // Show error message
                    showToast("Error:${result.message}")
                }

                is DataHandler.ServerError -> {
                    // Show error message
                    showToast("Error:${result.message}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun FavoriteArticleEntity.toAllNewsEntity(): ArticleEntity {
    return ArticleEntity(
        title = title,
        description = description,
        author = author,
        urlToImage = urlToImage,
        url = url,
        publishedAt = publishedAt
    )
}