package com.example.expenditurelogger.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.expenditurelogger.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CameraContent(
    cameraPermission: Boolean,
    onRequestPermission: () -> Unit,
    lastImageBitmap: Bitmap?,
    updateLastImageBitmap: (ImageProxy) -> Unit
) {
    if (cameraPermission) {
        if (lastImageBitmap != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = lastImageBitmap.asImageBitmap(),
                contentDescription = "Last Taken Picture",
                contentScale = ContentScale.Crop
            )
        } else {
            CameraPreviewContent(onPhotoCaptured = { imageProxy -> updateLastImageBitmap(imageProxy) })
        }
    } else {
        CameraPermissionRequestScreen(onRequestPermission)
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraPreviewContent(
    onPhotoCaptured: (ImageProxy) -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    Scaffold(
        Modifier.fillMaxSize(),
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = {
                    Log.d("DEV", "CameraPreviewContent: Picture taken!")
                    val mainExecutor = ContextCompat.getMainExecutor(context)
                    cameraController.takePicture(
                        mainExecutor,
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                onPhotoCaptured(imageProxy)
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
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(Color.BLACK)
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }.also { previewView ->
                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(
                        lifecycleOwner
                    )
                }
            })

        Canvas(
            modifier = Modifier.fillMaxSize(),
            onDraw = {
                val screenWidth = size.width
                val screenHeight = size.height
                val gridLineWidth = 1.5f * density
                val gridLineColor = androidx.compose.ui.graphics.Color.White.copy(0.5f)

                for (row in 1 until 3) {
                    val y = row * screenHeight / 3f
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(screenWidth, y),
                        strokeWidth = gridLineWidth
                    )
                }

                for (column in 1 until 3) {
                    val x = column * screenWidth / 3f
                    drawLine(
                        color = gridLineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, screenHeight),
                        strokeWidth = gridLineWidth
                    )
                }
            }
        )
    }
}

fun Bitmap.rotateBitmap(degrees: Float): Bitmap {
    return Bitmap.createBitmap(
        this,
        0,
        0,
        width,
        height,
        Matrix().apply { postRotate(degrees) },
        true
    )
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