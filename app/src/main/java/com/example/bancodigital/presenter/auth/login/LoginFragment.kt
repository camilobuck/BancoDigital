package com.example.bancodigital.presenter.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.bancodigital.R
import com.example.bancodigital.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        binding.textRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.textRecover.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverFragment)
        }
        binding.btnLogin.setOnClickListener { validateDta() }
    }

    private fun validateDta() {
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()

        if (email.isNotEmpty()) {
            if (password.isNotEmpty()) {
                Toast.makeText(requireContext(), "Login...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Digite sua senha", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Digite seu e-mail", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}