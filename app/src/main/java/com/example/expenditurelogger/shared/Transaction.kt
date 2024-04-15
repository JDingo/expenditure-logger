package com.example.expenditurelogger.shared

data class Transaction(
    var merchant: String = "",
    var date: String = "",
    var total: Float = 0f,
    var category_id: Int = 1
)
