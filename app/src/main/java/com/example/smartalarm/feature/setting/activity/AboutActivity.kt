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
import com.example.smartalarm.R
import com.example.smartalarm.databinding.ActivityAboutBinding
import androidx.core.net.toUri

class AboutActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAboutBinding
    private val binding get() = _binding

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
            binding.tvVersion.text = "Version $versionName"
        } catch (e: Exception) {
            binding.tvVersion.text = "Version 1.0.4"
        }

         // 3. Privacy Policy Click
        binding.btnPrivacy.setOnClickListener {
            openUrl("https://gist.githubusercontent.com/SachinCodeMan21/c5cf776c0587a46aef9db09d4248b134/raw/ad451674f43439f1af6611234d066aa942a6ff46/privacy_policy.md")
        }

         // 4. Terms of Service Click
        binding.btnTerms.setOnClickListener {
            openUrl("https://gist.githubusercontent.com/SachinCodeMan21/a1787191e29973b545821eb312580c6c/raw/c097ef3a003cfc6476315d2b638da4b78937abaf/terms_and_conditions.md")
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
        } catch (e: Exception) {
            Toast.makeText(this, "No browser found to open link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openPlayStore() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$appPackageName".toUri()))
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                )
            )
        }
    }

    private fun sendEmailSupport() {
        val version = binding.tvVersion.text.toString()
        val mailto =
            "mailto:sachinyadav211002@email.com" + "?subject=${Uri.encode("Support Request: Smart Alarm")}" + "&body=${
                Uri.encode("\n\n--- Device Info ---\nModel: ${Build.MODEL}\nSDK: ${Build.VERSION.SDK_INT}\nApp: $version\n\n")
            }"

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = mailto.toUri()
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        } catch (e: Exception) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }
}