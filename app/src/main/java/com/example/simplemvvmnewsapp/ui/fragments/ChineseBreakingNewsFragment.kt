package com.example.simplemvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue

import android.view.View
import android.widget.AbsListView

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.simplemvvmnewsapp.R
import com.example.simplemvvmnewsapp.adapters.NewsAdapter

import com.example.simplemvvmnewsapp.main.MainViewModel
import com.example.simplemvvmnewsapp.util.Constants.Companion.QUERY_PAGE_COUNT

import com.example.simplemvvmnewsapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_chinese_breaking_news.*
import kotlinx.android.synthetic.main.item_article_review.*
import kotlinx.android.synthetic.main.item_article_review.view.*

@AndroidEntryPoint
class ChineseBreakingNewsFragment: Fragment(R.layout.fragment_chinese_breaking_news) {

    private  val viewModel: MainViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter

    private val TAG = "Chinese News Fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_chineseBreakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.chineseBreakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success ->{
                    hideProgressBar()
                    response.data?.let { newsResponse ->

                       newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_COUNT +1
                        isLastPage = viewModel.chineseBreakingNewsPage == totalPages
                        if(isLastPage){
                            rvChineseBreakingNews.setPadding(0,0,0,0)
                        }

                    }

                }
                is Resource.Error ->{
                    hideProgressBar()
                    response.message?.let { message->
                        Log.e(TAG, "An error occurred: $message")
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
                else -> Unit
            }
        })



    }


private fun hideProgressBar(){
    paginationProgressBar.visibility = View.INVISIBLE
    isLoading = false
}

private fun showProgressBar(){
    paginationProgressBar.visibility = View.VISIBLE
    isLoading = true
}

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndIsNotLastPage = !isLoading && !isLastPage

            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount

            val isNotAtBeginning = firstVisibleItemPosition >= 0

            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_COUNT

            val shouldPaginate = isNotLoadingAndIsNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate){
                viewModel.getChineseBreakingNews("tw","zh")
                isScrolling = false
            }
        }
    }

private fun setupRecyclerView(){

    newsAdapter = NewsAdapter()
    newsAdapter.setTextSize(20F)
    rvChineseBreakingNews.apply {
        adapter = newsAdapter
        layoutManager = LinearLayoutManager(activity)
        addOnScrollListener(this@ChineseBreakingNewsFragment.scrollListener)
    }
}

}