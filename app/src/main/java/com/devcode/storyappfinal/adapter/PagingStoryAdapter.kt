package com.devcode.storyappfinal.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.devcode.storyappfinal.R
import com.devcode.storyappfinal.databinding.ItemRowBinding
import com.devcode.storyappfinal.db.StoryResponseRoom
import com.devcode.storyappfinal.ui.detailStories.DetailActivity

class PagingStoryAdapter : PagingDataAdapter<StoryResponseRoom, PagingStoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class ViewHolder(private val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun loadImage(url: String, imageView: ImageView, placeholder: Int) {
            val maxHeight = 535 * imageView.resources.displayMetrics.density
            Glide.with(imageView.context)
                .load(url)
                .override(Target.SIZE_ORIGINAL, maxHeight.toInt())
                .placeholder(placeholder)
                .error(placeholder)
                .into(imageView)
        }
        fun bind(story: StoryResponseRoom) {
            val name = story.name.replaceFirstChar { it.uppercase() }
            val apiUrlAvatar = "https://ui-avatars.com/api/?name=$name&size=128&background=random"
            binding.apply {
                loadImage(apiUrlAvatar, profileImage, R.drawable.ic_placeholder_photo)
                loadImage(story.photoUrl, ivItemPhoto, R.drawable.bg_post_image)
                tvItemName.text = name
                tvItemDescription.text =  story.description
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_STATE, story)
                intent.putExtra(DetailActivity.EXTRA_PHOTO, apiUrlAvatar)
                it.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryResponseRoom>() {
            override fun areItemsTheSame(oldItem: StoryResponseRoom, newItem: StoryResponseRoom): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryResponseRoom, newItem: StoryResponseRoom): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}