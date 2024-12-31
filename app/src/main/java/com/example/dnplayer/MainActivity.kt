package com.example.dnplayer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dnplayer.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        replaceFragment(HomeFragment())
        setContentView(binding.root)

        UIUtils.setGradientStatusBar(this, R.drawable.gradient_player_activity, R.id.main)
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.all_songs -> replaceFragment(AllSongFragment())
                R.id.favorite -> replaceFragment(FavoriteFragment())
                R.id.settings -> replaceFragment(SettingsFragment())
                else -> {

                }
            }

            true
        }

    }
    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_wrapper, fragment)
        fragmentTransaction.commit()



    }
}