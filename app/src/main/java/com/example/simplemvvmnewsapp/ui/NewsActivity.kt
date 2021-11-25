package com.example.simplemvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.RequestManager
import com.example.simplemvvmnewsapp.R
import com.example.simplemvvmnewsapp.ui.fragments.BreakingNewsFragment
import com.example.simplemvvmnewsapp.ui.fragments.SavedNewsFragment
import com.example.simplemvvmnewsapp.ui.fragments.SearchNewsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_news.*



@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

        lateinit var glide: RequestManager

        lateinit var toggle_button: ActionBarDrawerToggle

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_news)

        toggle_button = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle_button)
        toggle_button.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setupWithNavController(newsNavHostFragment.findNavController())


        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle_button.onOptionsItemSelected(item)){
            return true
        }

        val navController = findNavController(R.id.newsNavHostFragment)
        return item.onNavDestinationSelected(navController)

    }


}