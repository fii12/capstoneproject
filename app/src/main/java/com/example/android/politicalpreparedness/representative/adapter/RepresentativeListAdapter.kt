package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.RepresentativeListItemBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative

/**
 * A [ListAdapter] implementation used to display [Representative] instances.
 */
class RepresentativeListAdapter(private val clickListener: RepresentativeListener): ListAdapter<Representative, RepresentativeViewHolder>(RepresentativeDiffCallback()){

    // The list of representatives to display
    var representatives: List<Representative> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Function to determine the number of [Representative] instances
     */
    override fun getItemCount(): Int = representatives.size

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder.from(parent)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [RepresentativeViewHolder.itemView] to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val representativeItem = representatives[position]
        holder.itemView.setOnClickListener{
            clickListener.onClick(representativeItem)
        }
        holder.bind(representativeItem)
    }
}

/**
 * Custom implementation of a [RecyclerView.ViewHolder] to bind a [Representative] to a list item.
 */
class RepresentativeViewHolder(val binding: RepresentativeListItemBinding): RecyclerView.ViewHolder(binding.root) {

    /**
     * Binds a [Representative] to the view.
     */
    fun bind(item: Representative) {
        binding.representative = item
        binding.representativePhoto.setImageResource(R.drawable.ic_profile)
        item.official.channels?.let { showSocialLinks(it) }
        item.official.urls?.let { showWWWLinks(it) }
        binding.executePendingBindings()
    }

    /**
     * Companion object used to inflate the [RecyclerView.ViewHolder].
     */
    companion object {
        fun from(parent: ViewGroup): RepresentativeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RepresentativeListItemBinding.inflate(layoutInflater, parent, false)
            return RepresentativeViewHolder(binding)
        }
    }

    /**
     * Shows/hides social media links.
     */
    private fun showSocialLinks(channels: List<Channel>) {

        if (!channels.isNullOrEmpty()) {

            // Determine whether we show a Facebook icon
            val facebookUrl = getFacebookUrl(channels)
            if (!facebookUrl.isNullOrBlank()) {
                enableLink(binding.facebookIcon, facebookUrl)
            } else {
                binding.facebookIcon.visibility = View.INVISIBLE
            }

            // Determine whether we show a Twitter icon
            val twitterUrl = getTwitterUrl(channels)
            if (!twitterUrl.isNullOrBlank()) {
                enableLink(binding.twitterIcon, twitterUrl)
            } else {
                binding.twitterIcon.visibility = View.INVISIBLE
            }
        } else {
            binding.twitterIcon.visibility = View.INVISIBLE
            binding.facebookIcon.visibility = View.INVISIBLE
        }
    }

    /**
     * Shows/hides WWW links.
     */
    private fun showWWWLinks(urls: List<String>) {

        if (!urls.isNullOrEmpty()) {
            enableLink(binding.wwwIcon, urls.first())
        } else {
            binding.wwwIcon.visibility = View.INVISIBLE
        }
    }

    /**
     * Creates a Facebook URL.
     */
    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
                .map { channel -> "https://www.facebook.com/${channel.id}" }
                .firstOrNull()
    }

    /**
     * Creates a Twiter URL.
     */
    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
                .map { channel -> "https://www.twitter.com/${channel.id}" }
                .firstOrNull()
    }

    /**
     * Enables a link and displays the icon.
     */
    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    /**
     * Creates an [Intent] to open the browser with the specified URL.
     */
    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }
}

/**
 * Utility class used to determine if items in the [RecyclerView] are the same in some way.
 */
class RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {

    /**
     * Determines if items are the same.
     */
    override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return ((oldItem.office == newItem.office) && (oldItem.official == newItem.official))
    }

    /**
     * Determines if the items' contents are the same.
     */
    override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return oldItem == newItem
    }
}

/**
 *  An implementation of the click listener to allow users to interact with [RecyclerView] items.
 */
class RepresentativeListener(val clickListener: (representative: Representative) -> Unit) {
    fun onClick(representative: Representative) = clickListener(representative)
}