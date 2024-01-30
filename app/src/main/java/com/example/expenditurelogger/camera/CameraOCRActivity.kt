package com.example.expenditurelogger.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expenditurelogger.ui.theme.ExpenditureLoggerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraOCRActivity(onBackNavigationClick: () -> Unit) {
    var lastImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun updateLastImageBitmap(imageProxy: ImageProxy?) {

        if (imageProxy != null) {
            analyzeImage(imageProxy)
            lastImageBitmap = imageProxy
                .toBitmap()
                .rotateBitmap(imageProxy.imageInfo.rotationDegrees.toFloat())

            imageProxy.close()
        } else {
            lastImageBitmap = null
        }

        Log.d("INFO", "updateLastImageBitmap")

    }

    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    ExpenditureLoggerTheme {
        Scaffold()
        { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CameraContent(
                    cameraPermission = cameraPermissionState.status.isGranted,
                    onRequestPermission = cameraPermissionState::launchPermissionRequest,
                    lastImageBitmap = lastImageBitmap,
                    updateLastImageBitmap = { imageProxy -> updateLastImageBitmap(imageProxy) }
                )
            }
            FloatingActionButton(
                modifier = Modifier
                    .padding(24.dp),
                shape = CircleShape,
                onClick = {
                    if (lastImageBitmap != null) {
                        updateLastImageBitmap(null)
                    } else {
                        onBackNavigationClick()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate Back")
            }
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun analyzeImage(imageProxy: ImageProxy) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                Log.d("INFO", "analyze: Success")
                Log.d("INFO", visionText.text)
            }
            .addOnFailureListener { e ->
                Log.d("WARNING", "analyze: Failure")
                Log.d("WARNING", e.toString())
            }
    }
}