package com.example.smartalarm.feature.setting.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartalarm.core.utility.Constants
import com.example.smartalarm.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SettingActivity"
        private const val BINDING_NULL_ERROR = "$TAG ${Constants.BINDING_NULL}"
    }

    private var _binding: ActivitySettingBinding? = null
    private val binding get() = _binding?:error(BINDING_NULL_ERROR)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpToolbar()
        setUpClickListeners()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.settingToolbar)
        binding.settingToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setUpClickListeners() {

        // Set up the language selection card click listener
        binding.cardLanguage.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        }

        // Set up the language selection card click listener
        binding.cardAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        // Set up the language selection card click listener
        binding.cardHelp.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }


    }


}