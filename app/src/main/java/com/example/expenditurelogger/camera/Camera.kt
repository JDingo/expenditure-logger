package com.example.expenditurelogger.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.expenditurelogger.R
import com.example.expenditurelogger.ui.theme.ExpenditureLoggerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

@Composable
fun Camera(onBackNavigationClick: () -> Unit) {
    ExpenditureLoggerTheme {
        Scaffold()
        { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CameraActivity()
            }
            FloatingActionButton(
                modifier = Modifier
                    .padding(24.dp),
                onClick = onBackNavigationClick,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate Back")
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CameraActivity() {

    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    CameraContent(
        cameraPermission = cameraPermissionState.status.isGranted,
        onRequestPermission = cameraPermissionState::launchPermissionRequest
    )

}

@Composable
private fun CameraContent(
    cameraPermission: Boolean,
    onRequestPermission: () -> Unit
) {

    var lastImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun updateLastImageBitmap(newBitmap: Bitmap) {
        Log.d("INFO", "updateLastImageBitmap")
        lastImageBitmap = newBitmap
    }

    if (cameraPermission) {
        if (lastImageBitmap != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = lastImageBitmap!!.asImageBitmap(),
                contentDescription = "Last Taken Picture")
        } else {
            CameraPreviewContent(onPhotoCaptured = { newBitmap -> updateLastImageBitmap(newBitmap) })
        }
    } else {
        CameraPermissionRequestScreen(onRequestPermission)
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraPreviewContent(
    onPhotoCaptured: (Bitmap) -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    Scaffold(
        Modifier.fillMaxSize(),
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = {
                    Log.d("INFO", "Take Picture Button Pressed!")
                    val mainExecutor = ContextCompat.getMainExecutor(context)
                    cameraController.takePicture(
                        mainExecutor,
                        object : ImageCapture.OnImageCapturedCallback() {

                            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                /* TO-DO: Pass picture to image analyzer for OCR */
                                val lastImageBitmap = imageProxy
                                    .toBitmap()
                                    .rotateBitmap(imageProxy.imageInfo.rotationDegrees.toFloat())

                                onPhotoCaptured(lastImageBitmap)

                                analyzeImage(imageProxy)

                                imageProxy.close()
                            }
                        }
                    )
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_camera_alt_48),
                    contentDescription = "Take Picture"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues: PaddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setBackgroundColor(android.graphics.Color.BLACK)
                    scaleType = PreviewView.ScaleType.FILL_START
                }.also { previewView ->
                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            })
    }
}

private fun Bitmap.rotateBitmap(degrees: Float): Bitmap {
    return Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(degrees) }, true)
}

@Composable
private fun CameraPermissionRequestScreen(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Please grant camera permissions.")
        Button(onClick = onRequestPermission) {
            Text(text = "Ok")
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