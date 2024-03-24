package com.example.expenditurelogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expenditurelogger.camera.CameraActivity
import com.example.expenditurelogger.home.Home
import com.example.expenditurelogger.settings.ListOfMerchantsEdit
import com.example.expenditurelogger.settings.SettingsActivity
import com.example.expenditurelogger.ui.theme.ExpenditureLoggerTheme
import com.example.expenditurelogger.utils.FileHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpenditureLoggerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(false)
                }
            }
        }
    }
}

@Composable
fun MainApp(
    preview: Boolean
) {
    val navController = rememberNavController()

    if (!preview) {
        FileHandler.init(LocalContext.current)
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(
                onNavigateToCamera = { navController.navigate("camera") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("settings") {
            SettingsActivity(
                onBackNavigationClick = { navController.popBackStack() },
                onDataSendNavigation = { /* TODO */ },
                onMerchantEditNavigation = { navController.navigate("merchantListEdit") }
            )
        }

        composable("merchantListEdit") {
            ListOfMerchantsEdit(
                onBackNavigationClick = { navController.popBackStack() }
            )
        }

        composable("camera") {
            CameraActivity(
                onBackNavigationClick = { navController.popBackStack() }
            )
        }
    }

}

@Composable
@Preview
fun MainAppPreview() {
    MainApp(true)
}