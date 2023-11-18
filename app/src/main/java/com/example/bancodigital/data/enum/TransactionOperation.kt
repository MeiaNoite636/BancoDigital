package com.example.bancodigital.data.enum

enum class TransactionOperation {
    DEPOSIT,
    TRANSFER,
    RECHARGE;

    companion object{
        fun getOperation(operation: TransactionOperation): String {
            return when (operation) {
                DEPOSIT -> {
                    "DEPOSIT"
                }
                RECHARGE -> {
                    "RECARGA DE TELEFONE"
                }
                TRANSFER -> {
                    "TRANSFERÊNCIA"
                }
            }
        }
    }
}