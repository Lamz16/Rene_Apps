package com.lamz.reneapps.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lamz.reneapps.databinding.FragmentAccountBinding
import com.lamz.reneapps.ui.ViewModelFactory
import com.lamz.reneapps.ui.home.HomeViewModel

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.tvEmail.text = user.email
            binding.tvName.text = user.name
        }

        binding.btnOut.setOnClickListener{
            AlertDialog.Builder(requireActivity()).apply {
                setTitle("Yakin ingin keluar?")
                setPositiveButton("Ya") { _, _ ->
                    viewModel.logout()
                }
                setNegativeButton("Tidak") { _, _ -> }
                create()
                show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}