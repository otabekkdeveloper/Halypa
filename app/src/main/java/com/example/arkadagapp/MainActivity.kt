package com.example.arkadagapp

import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.arkadagapp.databinding.ActivityMainBinding
import com.example.arkadagapp.presentation.home.HomeFragment
import com.example.arkadagapp.presentation.quote.QuoteFragment
import com.example.arkadagapp.presentation.search.SearchFragment
import com.example.arkadagapp.presentation.settings.SettingsFragment
import com.example.arkadagapp.presentation.test.SynagFragment
import com.example.arkadagapp.utils.LocaleHelper
import com.example.arkadagapp.utils.ThemeHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)

        // Применить сохраненный язык
        LocaleHelper.updateResources(this, LocaleHelper.getLocale(this))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ СНАЧАЛА Edge-to-Edge (контент под status bar)
        setupEdgeToEdge()

        // ✅ ПОТОМ цвета status bar
        setupStatusBar()

        // Bottom Navigation не поднимается при клавиатуре
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Загрузить первый фрагмент
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            updateBottomNavIcons(R.id.nav_home)
        }

        // Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_search -> SearchFragment()
                R.id.nav_quote -> QuoteFragment()
                R.id.nav_questions -> SynagFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            updateBottomNavIcons(item.itemId)
            true
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val languageCode = LocaleHelper.getLocale(newBase)
        LocaleHelper.updateResources(newBase, languageCode)
        super.attachBaseContext(newBase)
    }

    private fun setupEdgeToEdge() {
        // ✅ Включить Edge-to-Edge (контент под status bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // ✅ Обработка insets для fragment_container
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentContainer) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // ✅ Применить padding только сверху (для status bar)
            view.setPadding(
                0,                    // left
                systemBars.top,       // top - отступ от status bar
                0,                    // right
                0                     // bottom - без отступа (есть bottom navigation)
            )

            insets
        }
    }

    private fun setupStatusBar() {
        window.apply {
            // Цвет Status Bar
            statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.app_color)

            // Цвет Navigation Bar (внизу)
            navigationBarColor = ContextCompat.getColor(this@MainActivity, R.color.app_color)

            // Белые иконки в Status Bar (для темного фона)
            WindowCompat.getInsetsController(this, decorView).apply {
                isAppearanceLightStatusBars = false // false = белые иконки
                isAppearanceLightNavigationBars = false // false = белые кнопки навигации
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateBottomNavIcons(selectedItemId: Int) {
        val menu = binding.bottomNavigation.menu

        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)

            val icon = when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (selectedItemId == R.id.nav_home) R.drawable.ic_home_bold
                    else R.drawable.ic_home
                }
                R.id.nav_search -> {
                    if (selectedItemId == R.id.nav_search) R.drawable.ic_search_bold
                    else R.drawable.ic_for_search
                }
                R.id.nav_quote -> {
                    if (selectedItemId == R.id.nav_quote) R.drawable.ic_quote_bold
                    else R.drawable.ic_quote
                }
                R.id.nav_questions -> {
                    if (selectedItemId == R.id.nav_questions) R.drawable.ic_test_bold
                    else R.drawable.ic_test
                }
                R.id.nav_settings -> {
                    if (selectedItemId == R.id.nav_settings) R.drawable.ic_settings_bold
                    else R.drawable.ic_settings
                }
                else -> null
            }

            icon?.let {
                menuItem.icon = ContextCompat.getDrawable(this, it)
            }
        }
    }

    fun setBottomNavVisibility(visible: Boolean) {
        binding.bottomNavigation.visibility = if (visible) View.VISIBLE else View.GONE
    }
}