package com.example.simplemvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplemvvmnewsapp.R
import com.example.simplemvvmnewsapp.adapters.NewsAdapter
import com.example.simplemvvmnewsapp.main.MainViewModel
import com.example.simplemvvmnewsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.simplemvvmnewsapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {

    private  val viewModel: MainViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter

    private val TAG = "Search News Fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job ?= null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable ?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchForNews(editable.toString())
                    }
                }
            }
        }
        etSearch.setOnKeyListener(object: View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event != null) {
                    if (event.action == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER
                    ) {
                        if (etSearch.text.toString().isNotEmpty()) {
                            viewModel.searchForNews(etSearch.text.toString())

                            etSearch.clearFocus()
                            etSearch.isCursorVisible = false

                            return true
                        }

                    }
                }
                return false
            }
        })

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
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
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}