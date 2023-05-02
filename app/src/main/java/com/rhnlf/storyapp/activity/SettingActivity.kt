package com.rhnlf.storyapp.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.databinding.ActivitySettingBinding
import com.rhnlf.storyapp.view.SettingViewModel
import com.rhnlf.storyapp.view.ViewModelFactory
import java.util.Locale

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var viewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.setting)
        supportActionBar?.setBackgroundDrawable(getColor(R.color.red).toDrawable())

        setupAction()
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(application)
        )[SettingViewModel::class.java]
    }

    private fun setupAction() {
        binding.apply {
            btnLogout.setOnClickListener {
                AlertDialog.Builder(this@SettingActivity).apply {
                    setTitle(getString(R.string.log_out))
                    setMessage(getString(R.string.are_you_sure_you_want_to_log_out))
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.logout()
                        val intent = Intent(this@SettingActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    setNegativeButton(getString(R.string.no), null)
                }.show()
            }

            btnChangeLanguage.text = Locale.getDefault().language
            btnChangeLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
    }
}