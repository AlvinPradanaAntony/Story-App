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
import com.bumptech.glide.request.target.Target


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
        val name = listStories[position].name.replaceFirstChar { it.uppercase() }
        val apiUrlAvatar = "https://ui-avatars.com/api/?name=$name&size=128&background=random"
        val apiPostImage = listStories[position].photoUrl
        holder.binding.apply {
            tvItemName.text = name
            tvItemDescription.text = listStories[position].description
            loadImage(apiUrlAvatar, profileImage, R.drawable.ic_placeholder_photo)
            loadImage(apiPostImage, ivItemPhoto, R.drawable.bg_post_image)
        }
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listStories[holder.adapterPosition]) }
    }

    private fun loadImage(url: String, imageView: ImageView, placeholder: Int) {
        val maxHeight = 535 * imageView.resources.displayMetrics.density
        Glide.with(imageView.context)
            .load(url)
            .override(Target.SIZE_ORIGINAL, maxHeight.toInt())
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