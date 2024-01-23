package com.example.expenditurelogger.home

import AppTopBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expenditurelogger.ui.theme.ExpenditureLoggerTheme

@Composable
fun Home() {
    ExpenditureLoggerTheme {
        Scaffold(
            topBar = { AppTopBar() }
        )
        { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    "Main Activity"
                )
            }
        }
    }
}