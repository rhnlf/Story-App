package com.rhnlf.storyapp.activity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.activity.MainActivity.Companion.EXTRA_STORY
import com.rhnlf.storyapp.data.remote.response.ListStoryItem
import com.rhnlf.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        title = getString(R.string.detail_story)
        supportActionBar?.setBackgroundDrawable(getColor(R.color.red).toDrawable())
    }

    private fun setupAction() {
        @Suppress("DEPRECATION") val item =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_STORY, ListStoryItem::class.java)
            } else {
                intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)
            }
        binding.apply {
            tvName.text = item?.name
            tvDescription.text = item?.description

            Glide.with(this@DetailActivity).load(item?.photoUrl).into(ivStory)
        }
    }
}