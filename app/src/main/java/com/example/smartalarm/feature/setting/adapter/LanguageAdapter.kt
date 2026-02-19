package com.example.smartalarm.feature.setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.databinding.ItemLanguageLayoutBinding
import com.example.smartalarm.feature.setting.model.LanguageItem

class LanguageAdapter(
    private val languages: List<LanguageItem>,
    private val onLanguageClick: (LanguageItem) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLanguageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {

        val language = languages[position]

        // Bind language name from string resource
        holder.binding.languageName.text = holder.itemView.context.getString(language.nameResId)

        // Bind the language icon (image resource)
        holder.binding.flagImage.setImageResource(language.iconResId)

        // Set click listener
        holder.itemView.setOnClickListener {
            onLanguageClick(language)
        }

    }

    override fun getItemCount(): Int = languages.size

    class LanguageViewHolder(val binding: ItemLanguageLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
