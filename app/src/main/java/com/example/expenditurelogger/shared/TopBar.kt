package com.example.expenditurelogger.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onActionClick: (() -> Unit)? = null,
    actionClickIcon: ImageVector? = null,
    onBackNavigationClick: (() -> Unit)? = null,
    title: String,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                title,
                maxLines = 1)
        },
        navigationIcon = {
            if (onBackNavigationClick != null) {
                IconButton(onClick = { onBackNavigationClick() }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Navigation to Back")
                }
            }
        },
        actions = {
            if (onActionClick != null && actionClickIcon != null) {
                IconButton(onClick = { onActionClick() }) {
                    Icon(imageVector = actionClickIcon, contentDescription = "Settings")
                }
            }
        }
    )
}

@Composable
@Preview
fun AppTopBarPreview() {
    TopBar(onActionClick = {}, actionClickIcon = Icons.Filled.Settings, title = "Expenditure Logger")
}




