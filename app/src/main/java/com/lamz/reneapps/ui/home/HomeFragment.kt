package com.lamz.reneapps.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lamz.reneapps.R
import com.lamz.reneapps.adapter.ListStoriesAdapter
import com.lamz.reneapps.adapter.LoadingStateAdapter
import com.lamz.reneapps.databinding.FragmentHomeBinding
import com.lamz.reneapps.ui.ViewModelFactory
import com.lamz.reneapps.ui.maps.MapsActivity

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var _binding: FragmentHomeBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAction()
        getStory()

        binding.swipe.setOnRefreshListener {
            getStory()
            Handler().postDelayed({
                binding.swipe.isRefreshing = false
            }, 2000)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getStory() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu1 -> {
                    val intent = Intent(context, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }


        val storyAdapter = ListStoriesAdapter()
        binding.rvListStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        binding.rvListStory.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storyAdapter
        }

        viewModel.getSession().observe(viewLifecycleOwner) {
            viewModel.getStories().observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.tvError.visibility = View.GONE
                    storyAdapter.submitData(lifecycle, it)
                } else {
                    binding.tvError.visibility = View.VISIBLE
                }

            }
        }

    }


    private fun startAction() {
        ObjectAnimator.ofFloat(binding.tagPage, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(binding.tagPage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()


        val name = ObjectAnimator.ofFloat(binding.tagPage, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(name)
            start()
        }

    }


}