package com.example.smartalarm.feature.setting.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartalarm.R
import com.example.smartalarm.databinding.ActivityAboutBinding
import androidx.core.net.toUri
import com.example.smartalarm.core.utility.Constants.BINDING_NULL

class AboutActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AboutActivity"
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
    }

    private var _binding: ActivityAboutBinding? = null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Setup Toolbar
        binding.aboutToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 2. Set Dynamic Version Name
        try {
            val versionName = packageManager.getPackageInfo(packageName, 0).versionName
            binding.tvVersion.text = getString(R.string.version,versionName)
        } catch (_: Exception) {
            binding.tvVersion.text = getString(R.string.version,"1.0")
        }

         // 3. Privacy Policy Click
        binding.btnPrivacy.setOnClickListener {
            openUrl("https://sachincodeman21.github.io/SmartAlarm/privacy.html")
        }

         // 4. Terms of Service Click
        binding.btnTerms.setOnClickListener {
            openUrl("https://sachincodeman21.github.io/SmartAlarm/terms.html")
        }

        // 5. Rate on Play Store Click
        binding.btnRate.setOnClickListener {
            openPlayStore()
        }

        // 6. Support Email (If you decide to add a Contact button later)
        // binding.btnSupport.setOnClickListener { sendEmailSupport() }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        try {
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(this,
                getString(R.string.no_browser_found_to_open_link), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openPlayStore() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$appPackageName".toUri()))
        } catch (_: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                )
            )
        }
    }
}