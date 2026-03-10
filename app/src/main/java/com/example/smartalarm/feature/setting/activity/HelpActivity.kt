package com.example.smartalarm.feature.setting.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartalarm.databinding.ActivityHelpBinding
import androidx.core.net.toUri
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants.BINDING_NULL

/**
 * An [AppCompatActivity] that provides a help/support interface for the app.
 *
 * This activity demonstrates edge-to-edge UI handling, toolbar back navigation,
 * and support email sending functionality. It uses view binding for
 * accessing UI elements safely.
 *
 * Features:
 * - Edge-to-edge layout support, adjusting padding for system bars.
 * - Toolbar with back navigation.
 * - A button to contact support via email with pre-filled device and app info.
 *
 * Lifecycle:
 * - [_binding] is initialized in [onCreate] and cleared in [onDestroy] to
 *   prevent memory leaks.
 *
 * Usage:
 * Simply start this activity from another component to show the help screen.
 *
 * Companion object constants:
 * - [TAG]: Logging tag for this activity.
 * - [BINDING_NULL_ERROR]: Error message used if binding is accessed after being nullified.
 */
class HelpActivity : AppCompatActivity() {

    companion object {
        /** Logging tag for HelpActivity */
        private const val TAG = "HelpActivity"

        /** Error message when binding is null */
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
    }

    /** Backing property for view binding */
    private var _binding: ActivityHelpBinding? = null

    /** Safe access to binding. Throws error if accessed after [onDestroy] */
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)

    /**
     * Called when the activity is created.
     * Sets up the UI, enables edge-to-edge display, toolbar navigation,
     * and the support email button.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle system bars for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Toolbar back navigation
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Contact support email
        binding.btnContactSupport.setOnClickListener {
            sendEmailSupport()
        }
    }

    /**
     * Called when the activity is destroyed.
     * Clears the binding reference to avoid memory leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Launches an email intent to contact support.
     * Pre-fills subject and body with device and app information.
     */
    private fun sendEmailSupport() {
        val version = packageManager.getPackageInfo(packageName, 0).versionName
        val mailto = "mailto:sachinyadav211002@email.com" +
                "?subject=${Uri.encode("Support Request: Smart Alarm")}" +
                "&body=${Uri.encode("\n\n--- Device Info ---\nModel: ${Build.MODEL}\nSDK: ${Build.VERSION.SDK_INT}\nApp: $version\n\n")}"

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = mailto.toUri()
        }

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
        } catch (_: Exception) {
            Toast.makeText(this, getString(R.string.no_email_app_found), Toast.LENGTH_SHORT).show()
        }
    }
}