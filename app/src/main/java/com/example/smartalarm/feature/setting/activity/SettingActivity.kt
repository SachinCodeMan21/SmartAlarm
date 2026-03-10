package com.example.smartalarm.feature.setting.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.databinding.ActivitySettingBinding

/**
 * Activity that displays the app settings and provides navigation to various
 * configuration and informational screens such as language selection, about, and help.
 *
 * This activity uses [ActivitySettingBinding] for view binding and ensures
 * proper cleanup of binding in [onDestroy] to avoid memory leaks.
 *
 * It also handles edge-to-edge display by applying system window insets.
 */
class SettingActivity : AppCompatActivity() {

    companion object {
        /** Tag used for logging purposes */
        private const val TAG = "SettingActivity"

        /** Error message when binding is unexpectedly null */
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
    }


    /** Backing property for view binding, nullable to allow cleanup in onDestroy */
    private var _binding: ActivitySettingBinding? = null

    /**
     * Non-nullable view binding property.
     *
     * Throws an error with [BINDING_NULL_ERROR] if accessed when [_binding] is null.
     */
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)

    // ----------------------------------
    // Lifecycle Methods
    // ----------------------------------

    /**
     * Called when the activity is starting.
     *
     * Initializes view binding, sets the content view, applies edge-to-edge window
     * insets, and sets up toolbar and click listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *     being shut down, this contains the data it most recently supplied.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply padding to handle edge-to-edge layouts
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpToolbar()
        setUpClickListeners()
    }

    /**
     * Called when the activity is destroyed.
     *
     * Clears the binding to avoid memory leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // ----------------------------------
    // Private Helper Methods
    // ----------------------------------

    /**
     * Sets up the toolbar as the activity's action bar and enables
     * navigation back handling.
     */
    private fun setUpToolbar() {
        setSupportActionBar(binding.settingToolbar)
        binding.settingToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Sets up click listeners for all the interactive cards in the settings screen.
     *
     * Each card navigates to a corresponding activity.
     */
    private fun setUpClickListeners() {
        binding.cardLanguage.setOnClickListener { openActivity(LanguageActivity::class.java) }
        binding.cardAbout.setOnClickListener { openActivity(AboutActivity::class.java) }
        binding.cardHelp.setOnClickListener { openActivity(HelpActivity::class.java) }
    }

    /**
     * Opens the specified activity.
     *
     * @param activity The [Class] of the activity to open.
     */
    private fun openActivity(activity: Class<*>) {
        startActivity(Intent(this, activity))
    }

}