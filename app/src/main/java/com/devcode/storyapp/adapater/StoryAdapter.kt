package com.devcode.storyapp.adapater

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devcode.storyapp.R
import com.devcode.storyapp.databinding.ItemRowBinding
import com.devcode.storyapp.db.ListStoryItem

class StoryAdapter(private val listStories: ArrayList<ListStoryItem>) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ViewHolder(var binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = listStories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = listStories[position].name
        val apiUrl = "https://ui-avatars.com/api/?name=$name&size=128&background=random"
        holder.binding.tvItemName.text = name
        holder.binding.tvItemDescription.text = listStories[position].description
        loadImage(apiUrl, holder.binding.profileImage, R.drawable.ic_placeholder_photo)
        loadImage(listStories[position].photoUrl, holder.binding.ivItemPhoto, R.drawable.bg_post_image)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listStories[holder.adapterPosition]) }
    }

    private fun loadImage(url: String, imageView: ImageView, placeholder: Int) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(placeholder)
            .error(placeholder)
            .into(imageView)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setListStory(stories: ArrayList<ListStoryItem>) {
        listStories.clear()
        listStories.addAll(stories)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }
}