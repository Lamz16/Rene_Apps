package com.lamz.reneapps.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.lamz.reneapps.data.ResultState
import com.lamz.reneapps.data.pref.UserModel
import com.lamz.reneapps.databinding.ActivityLoginBinding
import com.lamz.reneapps.ui.main.MainActivity
import com.lamz.reneapps.ui.ViewModelFactory
import com.lamz.reneapps.ui.welcome.WelcomeActivity
import es.dmoral.toasty.Toasty

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        setupView()
        setupAction()
        startAnimation()
        supportActionBar?.hide()
    }

    private fun startAnimation() {
        ObjectAnimator.ofFloat(binding?.imageView, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding?.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding?.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailLay =
            ObjectAnimator.ofFloat(binding?.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordLay =
            ObjectAnimator.ofFloat(binding?.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val btnLogin = ObjectAnimator.ofFloat(binding?.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title, message, emailLay, passwordLay, btnLogin)
            start()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding?.loginButton?.setOnClickListener {
            val email = binding?.emailEditText?.text.toString()
            val password = binding?.passwordEditText?.text.toString()

            viewModel.getData(email, password)
            viewModel.login.observe(this) { user ->
                if (user != null) {
                    when (user) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            user.data.message.let { it1 -> showToast(it1) }

                            viewModel.saveSession(
                                UserModel(
                                    email,
                                    user.data.loginResult.name,
                                    user.data.loginResult.token
                                )
                            )

                            AlertDialog.Builder(this).apply {
                                setTitle("Yeah!")
                                setMessage("Anda berhasil login. Selamat Datang ${user.data.loginResult.name}")
                                setPositiveButton("Lanjut") { _, _ ->
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }

                            showLoading(false)
                        }

                        is ResultState.Error -> {
                            showToast(user.error)
                            AlertDialog.Builder(this).apply {
                                setTitle("Oops !!!")
                                setMessage("Daftar dulu ya supaya bisa login")
                                setPositiveButton("Daftar") { _, _ ->
                                    val intent = Intent(context, WelcomeActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                setNegativeButton("Login") { _, _ -> }
                                create()
                                show()
                            }
                            showLoading(false)
                        }
                    }
                }
            }

        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toasty.success(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showToast2(message: String) {
        Toasty.warning(this, message, Toast.LENGTH_SHORT).show()
    }

}