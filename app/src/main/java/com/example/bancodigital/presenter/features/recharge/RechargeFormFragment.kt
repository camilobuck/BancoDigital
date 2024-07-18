package com.example.bancodigital.presenter.features.recharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bancodigital.R
import com.example.bancodigital.data.enum.TransactionOperation
import com.example.bancodigital.data.enum.TransactionType
import com.example.bancodigital.data.model.Recharge
import com.example.bancodigital.data.model.Transaction
import com.example.bancodigital.databinding.FragmentRechargeFormBinding
import com.example.bancodigital.util.BaseFragment
import com.example.bancodigital.util.StateView
import com.example.bancodigital.util.initToolbar
import com.example.bancodigital.util.showBottomSheet
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RechargeFormFragment : BaseFragment() {
    private var _binding: FragmentRechargeFormBinding? = null
    private val binding get() = _binding!!
    private val rechargeViewModel: RechargeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRechargeFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(toolbar = binding.toolbar, light = true)
        initListeners()
    }

    private fun initListeners() {
        binding.btnConfirm.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val amount = binding.editAmount.text.toString().trim()
        val phone = binding.editPhone.unMaskedText

        if (amount.isNotEmpty()) {
            if (phone?.isNotEmpty() == true) {
                if (phone.length == 11) {

                    hideKeyboard()

                    val recharge = Recharge(
                        amount = amount.toFloat(),
                        number = phone
                    )
                    saveRecharge(recharge)

                } else {
                    showBottomSheet(message = getString(R.string.text_phone_invalid))
                }
            } else {
                showBottomSheet(message = getString(R.string.text_phone_empty))
            }
        } else {
            Toast.makeText(requireContext(), "Digite um Valor!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveRecharge(recharge: Recharge) {
        rechargeViewModel.saveRecharge(recharge).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is StateView.Sucess -> {
                    stateView.data?.let { saveTransaction(it) }
                }

                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(message = stateView.message)
                }
            }
        }
    }

    private fun saveTransaction(recharge: Recharge) {
        val transaction = Transaction(
            id = recharge.id,
            operation = TransactionOperation.RECHARGE,
            date = recharge.date,
            amount = recharge.amount,
            type = TransactionType.CASH_OUT
        )
        rechargeViewModel.saveTransaction(transaction).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                }

                is StateView.Sucess -> {
                    val action = RechargeFormFragmentDirections
                        .actionRechargeFormFragmentToRechargeReceiptFragment(recharge.id, false)

                    findNavController().navigate(action)
                }

                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(message = stateView.message)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}