package com.example.arkadagapp

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.arkadagapp.databinding.ActivityMainBinding
import com.example.arkadagapp.presentation.home.HomeFragment
import com.example.arkadagapp.presentation.quote.QuoteFragment
import com.example.arkadagapp.presentation.search.SearchFragment
import com.example.arkadagapp.presentation.settings.SettingsFragment
import com.example.arkadagapp.presentation.test.TestFragment
import com.example.arkadagapp.utils.ThemeManager
import com.example.arkadagapp.utils.ThemePrefs

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        ThemeManager.applyTheme(
            ThemePrefs.load(this)
        )
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

        // Zagruzhaem pervyy fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            updateBottomNavIcons(R.id.nav_home) // Ustanovit' ikonku Home kak bold
        }

        // Obrabotchik BottomNavigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_search -> SearchFragment()
                R.id.nav_quote -> QuoteFragment()
                R.id.nav_questions -> TestFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            updateBottomNavIcons(item.itemId) // Obnovit' ikonki
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateBottomNavIcons(selectedItemId: Int) {
        val menu = binding.bottomNavigation.menu

        // Dlya kazhdogo item v menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)

            // Vybrat' pravilnuyu ikonku (bold ili regular)
            val icon = when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (selectedItemId == R.id.nav_home)
                        R.drawable.ic_home_bold
                    else
                        R.drawable.ic_home
                }
                R.id.nav_search -> {
                    if (selectedItemId == R.id.nav_search)
                        R.drawable.ic_search_bold
                    else
                        R.drawable.ic_for_search
                }
                R.id.nav_quote -> {
                    if (selectedItemId == R.id.nav_quote)
                        R.drawable.ic_quote_bold
                    else
                        R.drawable.ic_quote
                }
                R.id.nav_questions -> {
                    if (selectedItemId == R.id.nav_questions)
                        R.drawable.ic_test_bold
                    else
                        R.drawable.ic_test
                }
                R.id.nav_settings -> {
                    if (selectedItemId == R.id.nav_settings)
                        R.drawable.ic_settings_bold
                    else
                        R.drawable.ic_settings
                }
                else -> null
            }

            // Ustanovit' ikonku
            icon?.let {
                menuItem.icon = ContextCompat.getDrawable(this, it)
            }
        }
    }
}