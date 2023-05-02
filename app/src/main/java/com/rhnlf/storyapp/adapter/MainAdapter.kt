package com.rhnlf.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rhnlf.storyapp.data.remote.response.ListStoryItem
import com.rhnlf.storyapp.databinding.ItemStoryBinding

class MainAdapter : PagingDataAdapter<ListStoryItem, MainAdapter.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    class ViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.apply {
                tvName.text = data.name.toString()
                tvDescription.text = data.description.toString()
            }

            Glide.with(itemView.rootView).load(data.photoUrl).into(binding.ivStory)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(data)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem, newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}