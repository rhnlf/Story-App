package com.rhnlf.storyapp.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.data.local.User
import com.rhnlf.storyapp.databinding.ActivityLoginBinding
import com.rhnlf.storyapp.helper.Helper.Companion.isValidEmail
import com.rhnlf.storyapp.helper.Helper.Companion.isValidPassword
import com.rhnlf.storyapp.helper.ScreenState
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

        supportActionBar?.hide()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(application)
        )[LoginViewModel::class.java]

        viewModel.apply {
            isAnimate.observe(this@LoginActivity) {
                if (it.hasBeenHandled) showView()
                it.getContentIfNotHandled()?.let {
                    playAnimation()
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
                    val errorText = if (!edLoginPassword.text.toString()
                            .isValidPassword()
                    ) getString(R.string.minimum_8_characters)
                    else null
                    edLoginPassword.setError(errorText, null)
                }
            })

            btnLogin.setOnClickListener {
                binding.let {
                    val email = it.edLoginEmail.text.toString()
                    val password = it.edLoginPassword.text.toString()
                    viewModel.login(email, password).observe(this@LoginActivity) { state ->
                        when (state) {
                            is ScreenState.Loading -> {
                                showLoading(true)
                                showLoginInvalid(false)
                            }

                            is ScreenState.Success -> {
                                showLoading(false)
                                val name = state.data?.loginResult?.name as String
                                val token = state.data.loginResult.token as String
                                viewModel.saveUser(User(name, email, token))
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }

                            is ScreenState.Error -> {
                                showLoading(false)
                                val error = state.message as String
                                Log.e(TAG, "onError: $error")
                                if (error.contains("401")) showLoginInvalid(true)
                                else showLoginInvalid(true, error)
                            }

                            else -> {}
                        }
                    }
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

    private fun showView() {
        binding.apply {
            tvTitle.alpha = 1f
            tvEmail.alpha = 1f
            tilEmail.alpha = 1f
            tvPassword.alpha = 1f
            tilPassword.alpha = 1f
            btnLogin.alpha = 1f
            divider.alpha = 1f
            tvDoNotHaveAccount.alpha = 1f
            btnRegister.alpha = 1f
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showLoginInvalid(isError: Boolean, msg: String? = null) {
        val errorMessage = msg ?: getString(R.string.wrong_account)
        binding.tvError.text = errorMessage
        binding.cvLoginInvalid.visibility = if (isError) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}