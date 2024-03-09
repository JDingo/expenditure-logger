package com.example.expenditurelogger.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.expenditurelogger.ocr.TextOCR
import com.example.expenditurelogger.ui.theme.ExpenditureLoggerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.text.Text

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraOCRActivity(onBackNavigationClick: () -> Unit) {
    var lastImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var recognizedText by remember { mutableStateOf<Text?>(null) }
    var showAlertDialog by remember { mutableStateOf<Boolean>(false) }

    fun updateLastImageBitmap(imageProxy: ImageProxy?) {

        if (imageProxy != null) {
            TextOCR.analyzeImage(imageProxy) { result -> recognizedText = result; showAlertDialog = true }
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
                        recognizedText = null
                    } else {
                        onBackNavigationClick()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate Back")
            }
            if ( recognizedText != null ) {
                if (showAlertDialog) {
                    AlertDialogExample(
                        onDismissRequest = { showAlertDialog = false; recognizedText = null; updateLastImageBitmap(null) },
                        onConfirmation = { showAlertDialog = false },
                        dialogTitle = "Found text",
                        dialogText = recognizedText!!.text!!
                    )
                }
                if (lastImageBitmap != null) {
                    DrawBoundingBoxes(recognizedText!!, lastImageBitmap!!)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Warning, contentDescription = "Debug Info")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun DrawBoundingBoxes(boundingBoxes: Text, bitmap: Bitmap) {
    val imageSize = Size(bitmap.width.toFloat(), bitmap.height.toFloat())
    Log.d("IMGSIZE", imageSize.toString())

    val density = LocalDensity.current.density
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val canvasSize = (screenWidth / density)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        for (block in boundingBoxes.textBlocks) {
            // Scale the bounding box coordinates to fit the screen
            val scaleX = size.width / imageSize.width
            val scaleY = size.height / imageSize.height

            val targetAspectRatio = 9/20
            val pictureCropWidthChange = (imageSize.height * targetAspectRatio).toInt() / 2

            val blockFrame = block.boundingBox

            if (blockFrame != null) {
                Log.d("BOX",
                    "TOP: ${blockFrame.top}; LEFT: ${blockFrame.left}; WIDTH: ${blockFrame.width()}; HEIGHT: ${blockFrame.height()}; TEXT: ${block.text}"
                    )

                var left = 0f
                if (blockFrame.left.toFloat() < imageSize.width){
                    left = blockFrame.left.toFloat() - pictureCropWidthChange
                } else {
                    left = blockFrame.left.toFloat() + pictureCropWidthChange
                }

                drawRect(
                    color = Color.Red,
                    topLeft = Offset(
                        left * scaleX,
                        blockFrame.top.toFloat() * scaleY
                    ),
                    size = Size(
                        (blockFrame.width().toFloat()) * scaleX,
                        (blockFrame.bottom.toFloat() - blockFrame.top.toFloat())
                    ),
                    style = Stroke(4f)
                )
            }
            for (line in block.lines) {
                val lineFrame = line.boundingBox

                for (element in line.elements) {
                    val elementFrame = element.boundingBox

                }
            }
        }
    }
}