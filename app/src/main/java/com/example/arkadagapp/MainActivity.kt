package com.example.arkadagapp


import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.arkadagapp.databinding.ActivityMainBinding
import com.example.arkadagapp.presentation.home.HomeFragment
import com.example.arkadagapp.presentation.quote.QuoteFragment
import com.example.arkadagapp.presentation.search.SearchFragment
import com.example.arkadagapp.presentation.settings.SettingsFragment
import com.example.arkadagapp.presentation.test.TestFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // VAZHNO: Chtoby Bottom Navigation ne podnymalasya
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Загружаем первый фрагмент
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }


        // Обработчик BottomNavigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_search -> SearchFragment()
                R.id.nav_quote -> QuoteFragment()
                R.id.nav_questions -> SettingsFragment()
                R.id.nav_settings -> TestFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }

    }





    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()


    }
}

