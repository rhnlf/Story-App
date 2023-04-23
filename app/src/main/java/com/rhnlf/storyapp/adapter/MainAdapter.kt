package com.rhnlf.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rhnlf.storyapp.data.remote.response.ListStoryItem
import com.rhnlf.storyapp.databinding.ItemStoryBinding

class MainAdapter(private val listStory: List<ListStoryItem>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    class ViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvName.text = listStory[position].name.toString()
            tvDescription.text = listStory[position].description.toString()
        }

        Glide.with(holder.itemView.rootView).load(listStory[position].photoUrl)
            .into(holder.binding.ivStory)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStory[position])
        }
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}