package com.example.smartalarm.feature.setting.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants
import com.example.smartalarm.databinding.ActivityLanguageBinding
import com.example.smartalarm.feature.setting.adapter.LanguageAdapter
import com.example.smartalarm.feature.setting.model.LanguageItem
import com.example.smartalarm.feature.setting.manager.LanguageManager

class LanguageActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LanguageActivity"
        private const val BINDING_NULL_ERROR = "$TAG ${Constants.BINDING_NULL}"
    }

    private var _binding: ActivityLanguageBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException(BINDING_NULL_ERROR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpToolbar()
        setupLanguageRecyclerView()
    }

    private fun setUpToolbar() {
        binding.languageToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupLanguageRecyclerView() {
        val languageItems = getLanguageItemList()

        // Create and set up the language adapter with a click listener
        val languageAdapter = LanguageAdapter(languageItems) { language ->
            LanguageManager.setLanguage(this, language.code)
        }

        binding.languageRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@LanguageActivity)
            adapter = languageAdapter
        }
    }

    private fun getLanguageItemList(): List<LanguageItem> {
        // Predefined list of languages
        val languages = listOf(
            LanguageItem(nameResId = R.string.english, code = "en", iconResId = R.drawable.icon_english),
            LanguageItem(nameResId = R.string.spanish, code = "es", iconResId = R.drawable.icon_spanish),
            LanguageItem(nameResId = R.string.german, code = "de", iconResId = R.drawable.icon_german),
            LanguageItem(nameResId = R.string.french, code = "fr", iconResId = R.drawable.icon_french),
            LanguageItem(nameResId = R.string.portuguese, code = "pt", iconResId = R.drawable.icon_portuguese),
            LanguageItem(nameResId = R.string.korean, code = "ko", iconResId = R.drawable.icon_korean),
            LanguageItem(nameResId = R.string.chinese, code = "zh", iconResId = R.drawable.icon_chinese),
            LanguageItem(nameResId = R.string.japanese, code = "ja", iconResId = R.drawable.icon_japanese),
            LanguageItem(nameResId = R.string.hindi, code = "hi", iconResId = R.drawable.icon_hindi)
        )

        // Sort the list alphabetically based on the string resource name
        return languages.sortedBy { language ->
            // Use context.getString() to fetch the actual name, note that this should be done outside the data structure.
            getString(language.nameResId)
        }
    }

}
