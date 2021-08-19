package com.example.simplemvvmnewsapp.ui.fragments


import android.os.Bundle
import android.util.Log

import android.view.View

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.simplemvvmnewsapp.R
import com.example.simplemvvmnewsapp.adapters.NewsAdapter

import com.example.simplemvvmnewsapp.main.MainViewModel

import com.example.simplemvvmnewsapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_breaking_news.*

@AndroidEntryPoint
class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {

    private  val viewModel: MainViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter

    private val TAG = "Breaking News Fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success ->{
                    hideProgressBar()
                    response.data?.let { newsResponse ->

                       newsAdapter.differ.submitList(newsResponse.articles)

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
}

private fun showProgressBar(){
    paginationProgressBar.visibility = View.VISIBLE
}

private fun setupRecyclerView(){
    newsAdapter = NewsAdapter()
    rvBreakingNews.apply {
        adapter = newsAdapter
        layoutManager = LinearLayoutManager(activity)
    }
}
}