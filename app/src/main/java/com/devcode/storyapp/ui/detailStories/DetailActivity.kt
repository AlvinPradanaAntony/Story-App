package com.devcode.storyapp.ui.detailStories

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.devcode.storyapp.databinding.ActivityDetailBinding
import com.devcode.storyapp.db.ListStoryItem

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Post"

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
            startActivity(Intent.createChooser(shareUser, "Share Via"))
        }
    }

    private fun setupView() {
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STATE) as ListStoryItem
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_STATE = "extra_state"
    }
}