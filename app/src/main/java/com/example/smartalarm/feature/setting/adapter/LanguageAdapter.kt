package com.example.smartalarm.feature.setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.databinding.ItemLanguageLayoutBinding
import com.example.smartalarm.feature.setting.model.LanguageItem

/**
 * A [RecyclerView.Adapter] for displaying a list of languages with their names and flags.
 *
 * This adapter uses [ItemLanguageLayoutBinding] to bind each language's name and icon
 * in a recycler view item. Clicking on a language item triggers a callback.
 *
 * @property languages The list of [LanguageItem] to display.
 * @property onLanguageClick Lambda callback invoked when a language item is clicked.
 */
class LanguageAdapter(
    private val languages: List<LanguageItem>,
    private val onLanguageClick: (LanguageItem) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    /**
     * A [RecyclerView.ViewHolder] that holds the views for a language item.
     *
     * @property binding The view binding for the language item layout.
     */
    class LanguageViewHolder(val binding: ItemLanguageLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Called when a new [LanguageViewHolder] needs to be created.
     *
     * @param parent The parent [ViewGroup] into which the new view will be added.
     * @param viewType The view type of the new view.
     * @return A new instance of [LanguageViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLanguageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    /**
     * Binds a [LanguageItem] to the given [LanguageViewHolder].
     *
     * Sets the language name text, the flag image, and the click listener.
     *
     * @param holder The [LanguageViewHolder] to bind data to.
     * @param position The position of the item in the list.
     */
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

    /**
     * Returns the total number of language items.
     *
     * @return The size of the [languages] list.
     */
    override fun getItemCount(): Int = languages.size

}