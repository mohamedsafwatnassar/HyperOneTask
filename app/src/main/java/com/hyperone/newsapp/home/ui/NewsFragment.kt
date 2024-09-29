package com.hyperone.newsapp.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyperone.newsapp.R
import com.hyperone.newsapp.base.BaseFragment
import com.hyperone.newsapp.databinding.FragmentNewsBinding
import com.hyperone.newsapp.favorite.model.FavoriteArticleEntity
import com.hyperone.newsapp.favorite.viewModel.FavoriteViewModel
import com.hyperone.newsapp.home.adapter.HorizontalNewsAdapter
import com.hyperone.newsapp.home.adapter.VerticalNewsAdapter
import com.hyperone.newsapp.home.viewModel.NewsViewModel
import com.hyperone.newsapp.network.DataHandler
import com.hyperone.newsapp.utils.extentions.customNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFragment : BaseFragment() {

    private var _binding: FragmentNewsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by viewModels()
    private val favoriteVM: FavoriteViewModel by viewModels()

    private lateinit var horizontalNewsAdapter: HorizontalNewsAdapter
    private lateinit var verticalNewsAdapter: VerticalNewsAdapter

    private lateinit var recyclerLayoutManager: LinearLayoutManager

    var isRequestMade = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        initRecyclerViews()

        subscribeData()
    }

    private fun initViews() {
        // SearchView query listener for searching articles by keyword
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchArticles(it) } // Trigger search on query submit
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Spinner item selection listener for category filtering
        binding.categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCategory = parent?.getItemAtPosition(position).toString()
                    if (selectedCategory != "All") {
                        viewModel.filterArticlesByCategory(selectedCategory.lowercase()) // Trigger filtering by category
                    } else {
                        viewModel.filterArticlesByCategory("") // Show all articles if "All" is selected
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }
    }

    private fun initRecyclerViews() {
        binding.rvHorizontalBreakingNews.apply {
            horizontalNewsAdapter = HorizontalNewsAdapter()
            adapter = horizontalNewsAdapter
        }

        binding.rvVerticalNews.apply {
            recyclerLayoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            layoutManager = recyclerLayoutManager
            verticalNewsAdapter = VerticalNewsAdapter(
                itemNewClickCallBack = { article ->
                    val bundle = Bundle()
                    bundle.putString("Article_url", article.url)
                    findNavController().customNavigate(R.id.newsDetailsFragment, data = bundle)
                },
                onFavouriteCallback = { article, isFavorite, _ ->
                    if (isFavorite) {
                        favoriteVM.removeArticle(
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
                        favoriteVM.saveArticle(article)
                    }
                    article.isFavorite = !article.isFavorite // Toggle favorite status
                },
            )
            adapter = verticalNewsAdapter
            recyclerViewScrollListener()
        }
    }

    private fun subscribeData() {
        // Observing breaking news
        viewModel.breakingNews.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataHandler.ShowLoading -> {
                    // Show loading indicator
                    showLoading()
                }

                is DataHandler.Success -> {
                    hideLoading()
                    horizontalNewsAdapter.submitData(state.data)
                }

                is DataHandler.Error, is DataHandler.ServerError -> {
                    // Handle the error
                    hideLoading()
                }

                is DataHandler.HideLoading -> {
                    // Hide loading indicator
                    hideLoading()
                }
            }
        }

        // Observing all news
        viewModel.allNews.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataHandler.ShowLoading -> {
                    // Show loading indicator
                    showLoading()
                }

                is DataHandler.Success -> {
                    hideLoading()
                    println("state.data == ${state.data}")
                    verticalNewsAdapter.submitData(state.data)
                }

                is DataHandler.Error, is DataHandler.ServerError -> {
                    // Handle the error
                    hideLoading()
                }

                is DataHandler.HideLoading -> {
                    // Hide loading indicator
                    hideLoading()
                }
            }
        }

        // Set up observers
        viewModel.filteredArticles.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DataHandler.ShowLoading -> {
                    // Show loading spinner
                    showLoading()
                }

                is DataHandler.HideLoading -> {
                    // Show loading spinner
                    hideLoading()
                }

                is DataHandler.Success -> {
                    // Update RecyclerView with filtered articles
                    verticalNewsAdapter.submitData(result.data)
                }

                is DataHandler.Error, is DataHandler.ServerError -> {
                    // Show error message
                    showToast("Error: $result")
                }
            }
        }
    }

    private fun recyclerViewScrollListener() {
        binding.rvVerticalNews.addOnScrollListener(
            object :
                RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int,
                ) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (
                        recyclerLayoutManager.findFirstCompletelyVisibleItemPosition() != 0 &&
                        recyclerLayoutManager.itemCount - 1 == recyclerLayoutManager.findLastVisibleItemPosition() &&
                        newState == RecyclerView.SCROLL_STATE_IDLE &&
                        (
                                binding.rvVerticalNews.canScrollVertically(1) ||
                                        binding.rvVerticalNews.canScrollVertically(-1)
                                )
                    ) {
                        if (isRequestMade.not()) {
                            isRequestMade = true
                            viewModel.fetchAllNews()
                        }
                    }
                }
            },
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}