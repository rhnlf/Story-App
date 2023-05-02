package com.rhnlf.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rhnlf.storyapp.databinding.ItemLoadingBinding

class StateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<StateAdapter.StateViewHolder>() {

    class StateViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: StateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, loadState: LoadState
    ): StateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StateViewHolder(binding, retry)
    }
}