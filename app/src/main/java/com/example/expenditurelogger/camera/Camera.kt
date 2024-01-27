package com.example.expenditurelogger.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expenditurelogger.shared.AppTopBar
import com.example.expenditurelogger.ui.theme.ExpenditureLoggerTheme

@Composable
fun Camera(onBackNavigationClick: () -> Unit) {
    ExpenditureLoggerTheme {
        Scaffold(
            topBar = { AppTopBar(onBackNavigationClick = onBackNavigationClick) }
        )
        { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(text = "Camera Activity")
            }
        }
    }
}