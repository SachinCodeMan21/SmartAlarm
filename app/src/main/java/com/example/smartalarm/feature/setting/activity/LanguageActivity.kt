package com.example.smartalarm.feature.setting.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.databinding.ActivityLanguageBinding
import com.example.smartalarm.feature.setting.adapter.LanguageAdapter
import com.example.smartalarm.feature.setting.manager.LanguageManager
import com.example.smartalarm.feature.setting.utility.LanguageProvider

/**
 * Activity that displays a list of available languages and allows the user
 * to select the app's language.
 *
 * This activity handles:
 * - Edge-to-edge layout adjustments for system bars.
 * - Toolbar setup with back navigation.
 * - Displaying a list of languages in a RecyclerView.
 * - Updating the app language via [LanguageManager] when a language is selected.
 */
class LanguageActivity : AppCompatActivity() {

    companion object {
        /** Tag used for logging. */
        private const val TAG = "LanguageActivity"

        /** Error message thrown if the view binding is accessed when null. */
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
    }

    /** Backing property for view binding. Cleared in [onDestroy] to avoid memory leaks. */
    private var _binding: ActivityLanguageBinding? = null

    /** Non-nullable binding accessor. Throws [IllegalStateException] if binding is null. */
    private val binding get() = _binding ?: throw IllegalStateException(BINDING_NULL_ERROR)

    /**
     * Called when the activity is first created.
     *
     * - Sets up edge-to-edge layout.
     * - Inflates view binding.
     * - Applies system window insets.
     * - Initializes toolbar and language list RecyclerView.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpToolbar()
        setupLanguageRecyclerView()
    }

    /**
     * Called when the activity is destroyed.
     *
     * Clears the binding to prevent memory leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Sets up the toolbar with a back navigation icon.
     *
     * Clicking the navigation icon triggers the back press action.
     */
    private fun setUpToolbar() {
        binding.languageToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    /**
     * Initializes the language RecyclerView.
     *
     * - Retrieves the list of available languages from [LanguageProvider].
     * - Sets up [LanguageAdapter] with a click listener to update the app language.
     * - Configures the RecyclerView's layout manager and adapter.
     */
    private fun setupLanguageRecyclerView() {

        val languageItems = LanguageProvider.getLanguageList()

        val languageAdapter = LanguageAdapter(languageItems) { language ->
            LanguageManager.setLanguage(this, language.code)
        }

        binding.languageRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@LanguageActivity)
            adapter = languageAdapter
        }
    }
}