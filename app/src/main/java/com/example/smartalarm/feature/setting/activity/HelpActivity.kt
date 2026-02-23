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

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle System Bars (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Toolbar Back Navigation
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Email Support Logic
        binding.btnContactSupport.setOnClickListener {
            sendEmailSupport()
        }
    }

    private fun sendEmailSupport() {
        val version = packageManager.getPackageInfo(packageName, 0).versionName

        // This is clean, readable, and uses the more reliable URI method
        val mailto = "mailto:sachinyadav211002@email.com" +
                "?subject=${Uri.encode("Support Request: Smart Alarm")}" +
                "&body=${Uri.encode("\n\n--- Device Info ---\nModel: ${Build.MODEL}\nSDK: ${Build.VERSION.SDK_INT}\nApp: $version\n\n")}"

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