package com.example.expenditurelogger.inputForm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.example.expenditurelogger.shared.TopBar
import com.example.expenditurelogger.shared.Transaction
import com.example.expenditurelogger.shared.TransactionForm
import com.example.expenditurelogger.utils.NetworkWorker
import kotlinx.coroutines.launch

@Composable
fun InputForm(
    onBackNavigationClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    var currentTranscation = Transaction(merchant = "", date = "", total = 0.0f)

    fun handleSubmit(transaction: Transaction) {
        scope.launch {
            focusManager.clearFocus()
            NetworkWorker.postTranscation(transaction)
            snackbarHostState.showSnackbar("Sent!")
            currentTranscation = Transaction(merchant = "", date = "", total = 0.0f)
        }
    }

    fun handleCancel() {
        currentTranscation = Transaction(merchant = "", date = "", total = 0.0f)
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopBar(
                title = "List of Merchants",
                onBackNavigationClick = onBackNavigationClick,
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TransactionForm(parsedTransaction = currentTranscation, onSubmit = {handleSubmit(it)}, onCancel = { handleCancel() })
        }
    }
}