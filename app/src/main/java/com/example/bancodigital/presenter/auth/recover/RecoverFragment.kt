package com.example.bancodigital.presenter.auth.recover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bancodigital.R
import com.example.bancodigital.databinding.FragmentRecoverBinding
import com.example.bancodigital.util.BaseFragment
import com.example.bancodigital.util.FirebaseHelper
import com.example.bancodigital.util.StateView
import com.example.bancodigital.util.initToolbar
import com.example.bancodigital.util.showBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoverFragment : BaseFragment() {

    private var _binding: FragmentRecoverBinding? = null
    private val binding get() = _binding!!

    private val recoverViewModel: RecoverViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()
    }

    private fun initListeners() {
        binding.btnSend.setOnClickListener { validateDta() }
    }

    private fun validateDta() {
        val email = binding.editEmail.text.toString().trim()

        if (email.isNotEmpty()) {
            hideKeyboard()
            recoverAccount(email)

        } else {
            showBottomSheet(message = getString(R.string.text_email_empty))
        }
    }

    private fun recoverAccount(email: String) {

        recoverViewModel.recover(email).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is StateView.Sucess -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(
                        message =
                        getString(R.string.text_message_send_email_sucess_recover_fragment)
                    )
                }

                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(
                        message = getString(
                            FirebaseHelper.validError(
                                stateView.message ?: ""
                            )
                        )
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}