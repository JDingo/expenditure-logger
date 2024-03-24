package com.example.expenditurelogger.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expenditurelogger.shared.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsActivity(
    onBackNavigationClick: () -> Unit,
    onDataSendNavigation: () -> Unit,
    onMerchantEditNavigation: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TopBar(
            title = "Settings",
            onBackNavigationClick = onBackNavigationClick
        )

        Column {
            TextButton(
                onClick = { onDataSendNavigation() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Start,
                ){
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Data Send",
                        Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Sending Data")
                }
            }

            TextButton(
                onClick = { onMerchantEditNavigation() },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Start,
                ){
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Merchant Names",
                        Modifier.padding(end = 8.dp)
                    )
                    Text(text = "List of Known Merchants")
                }

            }
        }
    }


}

@Composable
@Preview
fun SettingsActivityPreview() {
    SettingsActivity(
        {},
        {},
        {}
    )
}