package com.example.expenditurelogger.shared

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TransactionForm(
    parsedTransaction: Transaction,
    onSubmit: (Transaction) -> Unit,
    onCancel: () -> Unit
) {
    var merchantName by remember { mutableStateOf(parsedTransaction.merchant ?: "") }
    var date by remember { mutableStateOf(parsedTransaction.date ?: "") }
    var transactionAmountString by remember { mutableStateOf(parsedTransaction.total.toString() ?: "") }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = merchantName,
            onValueChange = { merchantName = it },
            label = { Text("Merchant") }
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") }
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            value = transactionAmountString,
            onValueChange = {
                transactionAmountString = it
            },
            label = { Text("Total") }
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                onSubmit(Transaction(merchantName, date, transactionAmountString.toFloat()))
            }) {
                Text("Send")
            }

            OutlinedButton(
                onClick = { onCancel() }
            ) {
                Text("Cancel")
            }
        }
    }
}