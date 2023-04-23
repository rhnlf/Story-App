package com.rhnlf.storyapp.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.data.local.UserPreference
import com.rhnlf.storyapp.databinding.ActivityRegisterBinding
import com.rhnlf.storyapp.helper.Helper.Companion.dataStore
import com.rhnlf.storyapp.helper.Helper.Companion.isValidEmail
import com.rhnlf.storyapp.helper.Helper.Companion.isValidPassword
import com.rhnlf.storyapp.view.RegisterViewModel
import com.rhnlf.storyapp.view.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        )[RegisterViewModel::class.java]

        viewModel.apply {
            snackBarText.observe(this@RegisterActivity) {
                it.getContentIfNotHandled()?.let { snackBarText ->
                    Snackbar.make(window.decorView.rootView, snackBarText, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                this@RegisterActivity, R.color.red_light
                            )
                        ).setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.black))
                        .show()
                }
            }

            isLoading.observe(this@RegisterActivity) { isLoading ->
                showLoading(isLoading)
            }

            isUserCreated.observe(this@RegisterActivity) {
                if (it == true) {
                    AlertDialog.Builder(this@RegisterActivity).setTitle(getString(R.string.success))
                        .setMessage(getString(R.string.user_created_successfully))
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }.show()
                }
            }
        }
    }

    private fun setButton() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()
        val confirmPassword = binding.edRegisterConfirmPassword.text.toString()

        binding.btnRegister.isEnabled =
            true && name.isNotEmpty() && true && email.isNotEmpty() && email.isValidEmail() && true && password.isNotEmpty() && password.isValidPassword() && true && confirmPassword.isNotEmpty() && confirmPassword == password
    }

    private fun setupAction() {
        binding.apply {
            edRegisterName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButton()
                }

                override fun afterTextChanged(s: Editable?) {
                    edRegisterName.error = if (edRegisterName.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else null
                }
            })

            edRegisterEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButton()
                }

                override fun afterTextChanged(s: Editable?) {
                    edRegisterEmail.error = if (edRegisterEmail.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else if (!s.isValidEmail()) getString(R.string.email_is_invalid)
                    else null
                }
            })

            edRegisterPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButton()
                }

                override fun afterTextChanged(s: Editable?) {
                    edRegisterPassword.error = if (!edRegisterPassword.text.toString()
                            .isValidPassword()
                    ) getString(R.string.minimum_8_characters)
                    else if (edRegisterPassword.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else null
                }
            })

            edRegisterConfirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                    edRegisterConfirmPassword.error =
                        if (edRegisterPassword.text.toString() != edRegisterConfirmPassword.text.toString()) getString(
                            R.string.password_doesn_t_match
                        )
                        else null
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButton()
                }

                override fun afterTextChanged(s: Editable?) {
                    edRegisterConfirmPassword.error = if (edRegisterConfirmPassword.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else if (edRegisterPassword.text.toString() != edRegisterConfirmPassword.text.toString()) getString(
                        R.string.password_doesn_t_match
                    )
                    else null
                }
            })

            btnRegister.setOnClickListener {
                binding.let {
                    val name = it.edRegisterName.text.toString()
                    val email = it.edRegisterEmail.text.toString()
                    val password = it.edRegisterPassword.text.toString()
                    viewModel.register(name, email, password)
                }
            }

            btnLogin.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun playAnimation() {
        val greeting = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(2000)
        val name = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(2000)
        val nameInput = ObjectAnimator.ofFloat(binding.tilName, View.ALPHA, 1f).setDuration(2000)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(2000)
        val emailInput = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(2000)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(2000)
        val passwordInput =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(2000)
        val confirmPassword =
            ObjectAnimator.ofFloat(binding.tvConfirmPassword, View.ALPHA, 1f).setDuration(2000)
        val confirmPasswordInput =
            ObjectAnimator.ofFloat(binding.tilConfirmPassword, View.ALPHA, 1f).setDuration(2000)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(2000)
        val divider = ObjectAnimator.ofFloat(binding.divider, View.ALPHA, 1f).setDuration(2000)
        val text =
            ObjectAnimator.ofFloat(binding.tvAlreadyHaveAccount, View.ALPHA, 1f).setDuration(2000)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(2000)

        AnimatorSet().apply {
            playTogether(
                greeting,
                name,
                nameInput,
                email,
                emailInput,
                password,
                passwordInput,
                confirmPassword,
                confirmPasswordInput,
                register,
                divider,
                text,
                login
            )
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}