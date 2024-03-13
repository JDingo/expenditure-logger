package com.example.expenditurelogger.shared

data class Transaction(
    var merchantName: String = "",
    var date: String = "",
    var transactionAmount: Float = 0f
)
