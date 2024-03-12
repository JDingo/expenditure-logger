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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.expenditurelogger.ocr.TextOCR
import com.example.expenditurelogger.ocr.TextParser
import com.example.expenditurelogger.shared.Transaction
import com.example.expenditurelogger.shared.TransactionForm
import com.example.expenditurelogger.ui.theme.ExpenditureLoggerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.text.Text

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraActivity(onBackNavigationClick: () -> Unit) {
    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    var lastImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var recognizedText by remember { mutableStateOf<Text?>(null) }

    var parsedTransaction by remember {
        mutableStateOf<Transaction>(
            Transaction("", "", 0f)
        )
    }
    var transaction by remember {
        mutableStateOf<Transaction>(
            Transaction("", "", 0f)
        )
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    fun updateLastImageBitmap(imageProxy: ImageProxy?) {
        if (imageProxy != null) {
            TextOCR.analyzeImage(imageProxy) { result ->
                recognizedText = result;
                parsedTransaction = TextParser.parseTotal(recognizedText!!)
                showBottomSheet = true
            }

            lastImageBitmap = imageProxy
                .toBitmap()
                .rotateBitmap(imageProxy.imageInfo.rotationDegrees.toFloat())

            imageProxy.close()
        } else {
            lastImageBitmap = null
        }
    }

    fun resetState() {
        lastImageBitmap = null
        recognizedText = null
        parsedTransaction = Transaction("", "", 0f)
        transaction = Transaction("", "", 0f)
        showBottomSheet = false
    }

    fun handleSubmit(filledTransaction: Transaction) {
        transaction = filledTransaction
        Log.d(
            "DEV",
            "handleSubmit: Sent ${transaction.merchantName} ${transaction.date} ${transaction.transactionAmount}"
        )

        resetState()
    }

    fun handleCancel() {
        resetState()
    }

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
                    contentDescription = "Navigate Back"
                )
            }

            if (recognizedText != null && lastImageBitmap != null) {
                DrawBoundingBoxes(recognizedText!!, lastImageBitmap!!)
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                        resetState()
                    },
                    sheetState = sheetState
                ) {
                    TransactionForm(
                        parsedTransaction = parsedTransaction,
                        onSubmit = { handleSubmit(it) },
                        onCancel = { handleCancel() }
                    )
                }
            }
        }
    }
}

@Composable
fun DrawBoundingBoxes(boundingBoxes: Text, bitmap: Bitmap) {
    val imageSize = Size(bitmap.width.toFloat(), bitmap.height.toFloat())
    val textBlocks = boundingBoxes.textBlocks.sortedBy { it.boundingBox?.top }.toMutableList()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        val croppedImageWidth = imageSize.height * (size.width / size.height)

        val scaleX = size.width / croppedImageWidth
        val scaleY = size.height / imageSize.height

        drawRect(
            color = Color.Blue,
            topLeft = Offset(
                2f,
                2f
            ),
            size = Size(
                1078f,
                2260f
            ),
            style = Stroke(4f)
        )

        val widthChangeDelta = (imageSize.width - croppedImageWidth)

        for (block in textBlocks) {
            val boundingBox = block.boundingBox

            if (boundingBox != null) {
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(
                        (boundingBox.left - widthChangeDelta / 2) * scaleX,
                        boundingBox.top * scaleY
                    ),
                    size = Size(
                        boundingBox.width().toFloat() * scaleX,
                        boundingBox.height().toFloat() * scaleY
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