package com.example.expenditurelogger.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expenditurelogger.R
import com.example.expenditurelogger.shared.TopBar
import com.example.expenditurelogger.ui.theme.ExpenditureLoggerTheme

@Composable
fun Home(
    onNavigateToCamera: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToInputForm: () -> Unit
) {

    ExpenditureLoggerTheme {
        Scaffold(
            topBar = { TopBar(
                onActionClick = onNavigateToSettings,
                actionClickIcon = Icons.Default.Settings,
                title = "Expenditure Logger"
            ) }
        )
        { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.8F)
                    ) {
                        DataInputTypeSelectionFAB(
                            onClick = { onNavigateToCamera() },
                            iconId = R.drawable.baseline_photo_camera_24,
                            contentDescription = "Camera",
                            text = "From Camera"
                        )
                        DataInputTypeSelectionFAB(
                            onClick = { onNavigateToInputForm() },
                            iconId = R.drawable.baseline_edit_square_24,
                            contentDescription = "Manual Input",
                            text = "From Manual Input"
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun HomePreview() {
    Home(onNavigateToCamera = {}, onNavigateToSettings = {}, onNavigateToInputForm = {})
}

@Composable
fun DataInputTypeSelectionFAB(
    onClick: () -> Unit,
    iconId: Int,
    contentDescription: String,
    text: String
) {
    ExtendedFloatingActionButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = contentDescription
            )
        },
        text = { Text(text = text) }
    )
}