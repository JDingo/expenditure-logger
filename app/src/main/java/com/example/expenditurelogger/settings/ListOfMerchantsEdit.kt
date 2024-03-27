package com.example.expenditurelogger.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expenditurelogger.shared.TopBar
import com.example.expenditurelogger.utils.FileHandler
import kotlinx.coroutines.launch

@Composable
fun ListOfMerchantsEdit(
    onBackNavigationClick: () -> Unit,
    previewListOfMerchants: MutableList<String>? = null
) {
    var listOfMerchants: MutableList<String>

    if (previewListOfMerchants != null) {
        listOfMerchants = previewListOfMerchants
    } else {
        listOfMerchants = FileHandler.getListOfMerchants()
    }

    var value by remember { mutableStateOf(listOfMerchants.joinToString(separator = "\n")) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    val focusManager = LocalFocusManager.current

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopBar(
                title = "List of Merchants",
                onBackNavigationClick = onBackNavigationClick,
                onActionClick = {
                    scope.launch {
                        focusManager.clearFocus()
                        FileHandler.updateListOfMerchants(
                            value.split("\n").toMutableList(), context
                        )
                        snackbarHostState.showSnackbar("Saved!")
                    }
                },
                actionClickIcon = Icons.Default.Check
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            TextField(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            )
        }
    }
}

@Composable
@Preview
fun ListOfMerchantsEditPreview() {
    ListOfMerchantsEdit (
        {},
        previewListOfMerchants = mutableListOf("Alko", "S-Market", "K-Market")
    )
}