package com.example.bancodigital.presenter.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.bancodigital.data.model.Wallet
import com.example.bancodigital.domain.wallet.InitWalletUsecase
import com.example.bancodigital.util.StateView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val initWalletUsecase: InitWalletUsecase
) : ViewModel() {

    fun initWallet(wallet: Wallet) = liveData(Dispatchers.IO) {
        try {
            emit(StateView.Loading())
            initWalletUsecase.invoke(wallet)
            emit(StateView.Sucess(null))
        } catch (ex: Exception) {
            emit(StateView.Error(ex.message))
        }
    }
}