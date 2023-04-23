package com.rhnlf.storyapp.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.data.local.UserPreference
import com.rhnlf.storyapp.databinding.ActivityLoginBinding
import com.rhnlf.storyapp.helper.Helper.Companion.dataStore
import com.rhnlf.storyapp.helper.Helper.Companion.isValidEmail
import com.rhnlf.storyapp.helper.Helper.Companion.isValidPassword
import com.rhnlf.storyapp.view.LoginViewModel
import com.rhnlf.storyapp.view.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setupAction()
        setButton()
        playAnimation()

        supportActionBar?.hide()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(UserPreference.getInstance(dataStore), application)
        )[LoginViewModel::class.java]

        viewModel.apply {
            snackBarText.observe(this@LoginActivity) {
                it.getContentIfNotHandled()?.let { snackBarText ->
                    Snackbar.make(window.decorView.rootView, snackBarText, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                this@LoginActivity, R.color.red_light
                            )
                        ).setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.black))
                        .show()
                }
            }

            isLoading.observe(this@LoginActivity) { isLoading ->
                showLoading(isLoading)
            }

            loginError.observe(this@LoginActivity) { loginError ->
                showLoginInvalid(loginError)

                if (!loginError) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun setButton() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()

        binding.btnLogin.isEnabled =
            true && email.isNotEmpty() && email.isValidEmail() && true && password.isNotEmpty() && password.isValidPassword()
    }

    private fun setupAction() {
        binding.apply {
            edLoginEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButton()
                    showLoginInvalid(false)
                }

                override fun afterTextChanged(s: Editable?) {
                    edLoginEmail.error = if (edLoginEmail.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else if (!s.isValidEmail()) getString(R.string.email_is_invalid) else null
                }
            })

            edLoginPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButton()
                    showLoginInvalid(false)
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            btnLogin.setOnClickListener {
                binding.let {
                    val email = it.edLoginEmail.text.toString()
                    val password = it.edLoginPassword.text.toString()
                    viewModel.login(email, password)
                }
            }

            btnRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun playAnimation() {
        val greeting = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(2000)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(2000)
        val emailInput = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(2000)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(2000)
        val passwordInput =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(2000)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(2000)
        val divider = ObjectAnimator.ofFloat(binding.divider, View.ALPHA, 1f).setDuration(2000)
        val text =
            ObjectAnimator.ofFloat(binding.tvDoNotHaveAccount, View.ALPHA, 1f).setDuration(2000)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(2000)

        AnimatorSet().apply {
            playTogether(
                greeting, email, emailInput, password, passwordInput, login, divider, text, register
            )
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showLoginInvalid(isError: Boolean) {
        binding.cvLoginInvalid.visibility = if (isError) View.VISIBLE else View.GONE
    }
}