package com.rhnlf.storyapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.adapter.MainAdapter
import com.rhnlf.storyapp.adapter.StateAdapter
import com.rhnlf.storyapp.data.remote.response.ListStoryItem
import com.rhnlf.storyapp.databinding.ActivityMainBinding
import com.rhnlf.storyapp.view.MainViewModel
import com.rhnlf.storyapp.view.ViewModelFactory
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var adapter = MainAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setupAction()
        title = getString(R.string.app_name)
        supportActionBar?.setBackgroundDrawable(getColor(R.color.red).toDrawable())
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(this).setMessage(getString(R.string.are_you_sure_you_want_to_exit))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                finish()
                exitProcess(0)
            }.setNegativeButton(getString(R.string.no), null).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(
                    intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
                )
            }

            R.id.action_refresh -> {
                setupAction()
                initViewModel()
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            R.id.action_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(
                    intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(application)
        )[MainViewModel::class.java]

        viewModel.apply {
            isLoading.observe(this@MainActivity) { isLoading ->
                showLoading(isLoading)
            }

            snackBarText.observe(this@MainActivity) {
                it.getContentIfNotHandled()?.let { snackBarText ->
                    Snackbar.make(
                        window.decorView.rootView, snackBarText, Snackbar.LENGTH_SHORT
                    ).setBackgroundTint(
                        ContextCompat.getColor(
                            this@MainActivity, R.color.red_light
                        )
                    ).setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity, R.color.black
                        )
                    ).show()
                }
            }

            story.observe(this@MainActivity) { pagingData ->
                Log.d(TAG, "Submitting list to adapter  THIS")
                adapter.submitData(lifecycle, pagingData)
            }
        }
    }

    private fun setStoryList() {
        adapter = MainAdapter()
        adapter.setOnItemClickCallback(object : MainAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(EXTRA_STORY, data)
                startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle()
                )
            }
        })
        binding.rvStoryList.adapter =
            adapter.withLoadStateFooter(footer = StateAdapter { adapter.retry() })
    }

    private fun setupAction() {
        setStoryList()

        val layoutManager = LinearLayoutManager(this)
        binding.apply {
            rvStoryList.layoutManager = layoutManager
            rvStoryList.setHasFixedSize(true)

            fabPost.setOnClickListener {
                val intent = Intent(this@MainActivity, PostStoryActivity::class.java)
                startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity).toBundle()
                )
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_STORY = "story"
        private const val TAG = "ListStoryActivity"
    }
}