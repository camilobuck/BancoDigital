package com.example.bancodigital.data.enum

import com.example.bancodigital.data.enum.TransactionOperation.DEPOSIT
import com.example.bancodigital.data.enum.TransactionOperation.RECHARGE

enum class TransactionType {
    CASH_IN,
    CASH_OUT;

    companion object {
        fun getType(operation: TransactionOperation): Char {
            return when (operation) {
                DEPOSIT -> {
                    'D'
                }

                RECHARGE -> {
                    'R'
                }
            }
        }
    }
}