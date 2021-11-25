package com.example.simplemvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.simplemvvmnewsapp.R
import com.example.simplemvvmnewsapp.adapters.SongAdapter
import com.example.simplemvvmnewsapp.main.MusicViewModel
import com.example.simplemvvmnewsapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicFragment: Fragment(R.layout.fragment_music) {

    lateinit var musicViewModel: MusicViewModel

    @Inject
    lateinit var musicAdapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        musicViewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)
    }

    private fun subscribeToObserver(){
        musicViewModel.mediaItems.observe(viewLifecycleOwner){ Resource ->
            when(Resource){
                is Resource.Success->{

                }
                is Resource.Error -> Unit

                is Resource.Loading->{

                }
                else -> Unit

            }

        }
    }
}