package com.lamz.reneapps.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.lamz.reneapps.custom_view.MyButton
import com.lamz.reneapps.custom_view.MyEditText
import com.lamz.reneapps.custom_view.MyEmailText
import com.lamz.reneapps.data.ResultState
import com.lamz.reneapps.databinding.ActivitySignUpBinding
import com.lamz.reneapps.ui.ViewModelFactory

class SignUpActivity : AppCompatActivity() {
    private var _binding: ActivitySignUpBinding? = null
    private val binding get() = _binding
    private lateinit var myButton: MyButton
    private lateinit var myEditText: MyEditText
    private lateinit var myEmailText: MyEmailText

    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        setupAction()
        playAnimation()

        myButton = binding?.signupButton!!
        myEditText= binding?.passwordEditText!!
        myEmailText = binding?.emailEditText!!

        setMyButtonEnable()
        launchMyEmailText()
        launchMyEditText()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.imageView, View.TRANSLATION_X, -30f,30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding?.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditLay = ObjectAnimator.ofFloat(binding?.nameEditTextLayout, View.ALPHA , 1f).setDuration(100)
        val emailLay = ObjectAnimator.ofFloat(binding?.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordLay = ObjectAnimator.ofFloat(binding?.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val btnSign = ObjectAnimator.ofFloat(binding?.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title,nameEditLay,emailLay, passwordLay, btnSign)
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
        binding?.signupButton?.setOnClickListener {
            val email = binding?.emailEditText?.text.toString()
            val name = binding?.nameEditText?.text.toString()
            val password = binding?.passwordEditText?.text.toString()

            viewModel.uploadData(name,email,password)
            viewModel.upload.observe(this) {result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            result.data.message.let { it1 -> showToast(it1) }

                            AlertDialog.Builder(    this).apply {
                                setTitle("Yeah!")
                                setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan belajar coding.")
                                setPositiveButton("Lanjut") { _, _ ->
                                    finish()
                                }
                                create()
                                show()
                            }
                            showLoading(false)
                        }

                        is ResultState.Error -> {
                            showToast(result.error)
                            showLoading(false)

                        }
                    }
                }

            }

        }
    }

    private fun setMyButtonEnable() {
        val result = myEditText.text
        myButton.isEnabled = result?.length!! > 7
    }


    private fun launchMyEditText(){
        myEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun launchMyEmailText(){
        myEmailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressIndicator?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}