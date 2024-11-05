package com.zuba.stroyapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zuba.stroyapp2.databinding.ItemRowStoryBinding
import com.zuba.stroyapp2.response.ListStory

class MainAdapter : PagingDataAdapter<ListStory, MainAdapter.ViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickListener: OnItemClickListener

    class ViewHolder(private var binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStory, x: OnItemClickListener) {
            Glide.with(itemView.context).load(data.photoUrl).into(binding.imgStory)
            binding.txtName.text = data.name
            binding.txtDesc.text = data.description
            binding.cardView.setOnClickListener {
                x.onItemClicked(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) holder.bind(data, onItemClickListener)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(data: ListStory)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}