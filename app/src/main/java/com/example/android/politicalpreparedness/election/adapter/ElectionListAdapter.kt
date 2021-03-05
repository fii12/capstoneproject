package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionListItemBinding
import com.example.android.politicalpreparedness.network.models.Election

/**
 * A [ListAdapter] implementation used to display [Election] instances.
 */
class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    // The list of elections to display
    var elections: List<Election> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Function to determine the number of [Election] instances
     */
    override fun getItemCount(): Int = elections.size

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
    */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ElectionViewHolder.itemView] to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val electionItem = elections[position]
        holder.itemView.setOnClickListener{
            clickListener.onClick(electionItem)
        }
        holder.bind(electionItem)
    }
}

/**
 * Custom implementation of a [RecyclerView.ViewHolder] to bind an [Election] to a list item.
 */
class ElectionViewHolder(private val electionListItemBinding: ElectionListItemBinding) : RecyclerView.ViewHolder(electionListItemBinding.root){

    /**
     * Binds an [Election] to the view.
     */
    fun bind(item: Election) {
        electionListItemBinding.election = item
        electionListItemBinding.executePendingBindings()
    }

    /**
     * Companion object used to inflate the [RecyclerView.ViewHolder].
     */
    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ElectionListItemBinding.inflate(layoutInflater, parent, false)
            return ElectionViewHolder(binding)
        }
    }
}

/**
 * Utility class used to determine if items in the [RecyclerView] are the same in some way.
 */
class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {

    /**
     * Determines if items are the same using the id of the items.
     */
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Determines if the items' contents are the same.
     */
    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }
}

/**
 *  An implementation of the click listener to allow users to interact with [RecyclerView] items.
 */
class ElectionListener(val clickListener: (election: Election) -> Unit) {

    fun onClick(election: Election) = clickListener(election)
}