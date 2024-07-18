package com.example.bancodigital.presenter.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bancodigital.R
import com.example.bancodigital.data.enum.TransactionOperation
import com.example.bancodigital.data.enum.TransactionType
import com.example.bancodigital.data.model.Transaction
import com.example.bancodigital.databinding.FragmentHomeBinding
import com.example.bancodigital.util.FirebaseHelper
import com.example.bancodigital.util.GetMask
import com.example.bancodigital.util.StateView
import com.example.bancodigital.util.showBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapterTransaction: TransactionAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configRecyclerView()
        getTransactions()
        initListeners()
    }

    private fun initListeners() {
        binding.btnLogout.setOnClickListener {
            FirebaseHelper.getAuth().signOut()
            findNavController().navigate(R.id.action_homeFragment_to_authentication)
        }
        binding.btnShowAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_extractFragment)
        }
        binding.cardDeposit.setOnClickListener() {
            findNavController().navigate(R.id.action_homeFragment_to_depositFormFragment)
        }
        binding.cardRecharge.setOnClickListener() {
            findNavController().navigate(R.id.action_homeFragment_to_rechargeFormFragment)
        }
        binding.cardExtract.setOnClickListener() {
            findNavController().navigate(R.id.action_homeFragment_to_extractFragment)
        }
        binding.cardTransfer.setOnClickListener() {
            findNavController().navigate(R.id.action_homeFragment_to_transferUserListFragment)
        }
        binding.cardProfile.setOnClickListener() {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun configRecyclerView() {
        adapterTransaction = TransactionAdapter(requireContext()) { transaction ->
            when (transaction.operation) {
                TransactionOperation.DEPOSIT -> {
                    val action = HomeFragmentDirections
                        .actionHomeFragmentToDepositReceiptFragment(transaction.id, true)
                    findNavController().navigate(action)
                }

                TransactionOperation.RECHARGE -> {
                    val action = HomeFragmentDirections
                        .actionHomeFragmentToRechargeReceiptFragment(transaction.id, true)
                    findNavController().navigate(action)
                }

                else -> {

                }
            }
        }

        with(binding.rvTransactions) {
            setHasFixedSize(true)
            adapter = adapterTransaction
        }
    }

    private fun getTransactions() {
        homeViewModel.getTransactions().observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is StateView.Sucess -> {
                    binding.progressBar.isVisible = false
                    adapterTransaction.submitList(stateView.data?.reversed())
                    showBalance(stateView.data ?: emptyList())
                }

                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(message = stateView.message)
                }
            }
        }
    }

    private fun showBalance(transactions: List<Transaction>) {
        var cashIn = 0f
        var cashOut = 0f

        transactions.forEach { transaction ->
            if (transaction.type == TransactionType.CASH_IN) {
                cashIn += transaction.amount
            } else {
                cashOut += transaction.amount
            }
        }
        binding.textBalance.text = getString(
            R.string.text_formated_value, GetMask.getFormatedValue(
                cashIn - cashOut
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}