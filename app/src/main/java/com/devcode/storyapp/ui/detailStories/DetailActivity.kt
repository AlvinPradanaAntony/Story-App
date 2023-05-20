package com.devcode.storyapp.ui.detailStories

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.devcode.storyapp.R
import com.devcode.storyapp.databinding.ActivityDetailBinding
import com.devcode.storyapp.db.StoryResponseRoom
import com.devcode.storyapp.remote.ListStoryItem

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarId)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Detail"
        setupView()
        setupAction()
    }

    private fun setupAction(){
        binding.btnDetailShare.setOnClickListener {
            val shareUser = Intent(Intent.ACTION_SEND)
            shareUser.type = "text/plain"
            val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STATE) as ListStoryItem
            val textOnShare = story.photoUrl
            shareUser.putExtra(Intent.EXTRA_TEXT, textOnShare)
            startActivity(Intent.createChooser(shareUser, R.string.text_to_share.toString()))
        }
    }

    private fun setupView() {
        val story = intent.getParcelableExtra<StoryResponseRoom>(EXTRA_STATE) as StoryResponseRoom
        val photo = intent.getStringExtra(EXTRA_PHOTO).toString()
        loadImage(photo, binding.ivPhotoProfile, R.drawable.ic_placeholder_photo)
        loadImage(story.photoUrl, binding.ivDetailPhoto, R.drawable.ic_placeholder_photo)
        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
    }

    private fun loadImage(url: String, imageView: ImageView, placeholder: Int) {
        Glide.with(this)
            .load(url)
            .placeholder(placeholder)
            .error(placeholder)
            .into(imageView)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_STATE = "extra_state"
        const val EXTRA_PHOTO = "extra_photo"
    }
}