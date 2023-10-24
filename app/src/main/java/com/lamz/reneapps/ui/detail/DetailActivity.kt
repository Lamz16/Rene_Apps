package com.lamz.reneapps.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.lamz.reneapps.R
import com.lamz.reneapps.data.ResultState
import com.lamz.reneapps.databinding.ActivityDetailBinding
import com.lamz.reneapps.ui.ViewModelFactory
import com.lamz.reneapps.ui.welcome.WelcomeActivity


@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpView()

        binding?.swipe?.setOnRefreshListener {
            setUpView()

            Handler().postDelayed({
                binding?.swipe?.isRefreshing = false
            }, 2000)
        }

        supportActionBar?.hide()

    }

    private fun setUpView() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            val detail = intent.getStringExtra(EXTRA_ID)
            if (detail != null) {
                viewModel.getDetailStories(user.token, detail)
            }

            viewModel.detailStory.observe(this) { story ->
                if (story != null) {
                    when (story) {
                        is ResultState.Loading -> {
                            binding?.progressBar?.visibility = View.VISIBLE
                        }

                        is ResultState.Success -> {
                            binding?.progressBar?.visibility = View.GONE
                            binding?.tvAuth?.text = story.data.story.name
                            binding?.tvDesc?.text = resources.getString(
                                R.string.text_desc,
                                story.data.story.description
                            )
                            Glide.with(this).load(story.data.story.photoUrl)
                                .centerCrop()
                                .into(binding?.imgDetail!!)
                        }

                        is ResultState.Error -> {
                            binding?.progressBar?.visibility = View.GONE
                            binding?.tvDesc?.text =
                                resources.getString(R.string.text_desc, story.error)
                            Toast.makeText(this, story.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    companion object {

        const val EXTRA_ID = "extra_id"
    }
}