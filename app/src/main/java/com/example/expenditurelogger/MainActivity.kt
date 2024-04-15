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
import com.example.expenditurelogger.ocr.TextParser
import com.example.expenditurelogger.settings.ListOfMerchantsEdit
import com.example.expenditurelogger.settings.SettingsActivity
import com.example.expenditurelogger.settings.UrlEdit
import com.example.expenditurelogger.ui.theme.ExpenditureLoggerTheme
import com.example.expenditurelogger.utils.FileWorker
import com.example.expenditurelogger.utils.NetworkWorker

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
        NetworkWorker.init(LocalContext.current)
    }

    val urlFileWorker = FileWorker(LocalContext.current, "BACKEND_URL.txt")
    val merchantFileWorker = FileWorker(LocalContext.current, "listOfMerchants.txt")

    val textParser = TextParser(merchantFileWorker)

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
                onDataSendNavigation = { navController.navigate("urlEdit") },
                onMerchantEditNavigation = { navController.navigate("merchantListEdit") },
            )
        }

        composable("merchantListEdit") {
            ListOfMerchantsEdit(
                onBackNavigationClick = { navController.popBackStack() },
                merchantFileWorker = merchantFileWorker
            )
        }

        composable("urlEdit") {
            UrlEdit(
                onBackNavigationClick = { navController.popBackStack() },
                urlFileWorker = urlFileWorker
            )
        }

        composable("camera") {
            CameraActivity(
                onBackNavigationClick = { navController.popBackStack() },
                textParser
            )
        }
    }

}

@Composable
@Preview
fun MainAppPreview() {
    MainApp(true)
}