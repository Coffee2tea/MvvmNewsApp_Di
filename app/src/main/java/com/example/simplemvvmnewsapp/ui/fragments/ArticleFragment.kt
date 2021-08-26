package com.example.simplemvvmnewsapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.simplemvvmnewsapp.R
import com.example.simplemvvmnewsapp.main.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_article.*

@AndroidEntryPoint
class ArticleFragment: Fragment(R.layout.fragment_article) {

    private  val viewModel: MainViewModel by viewModels()
    val args: ArticleFragmentArgs by navArgs()
    var itemExistence: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val article = args.article


        webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        viewModel.getAllSavedNews().observe(viewLifecycleOwner, Observer { articles ->

            if(articles != null){
                for(item in articles){
                    if (item.url == article.url){
                        itemExistence = true
                        break
                    }else{
                        itemExistence = false
                    }
                }
            }

        })


        fab.setOnClickListener{


              if(itemExistence){
                    Snackbar.make(view, "Article already existed", Snackbar.LENGTH_SHORT).show()
                }else {
                    viewModel.saveArticle(article)
                    Snackbar.make(view, "Article Saved Successfully", Snackbar.LENGTH_SHORT).show()
                    fab.isClickable = false
                    fab.alpha = 0.5f

                }


           }
        }


}

