package com.example.expenditurelogger.ocr

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextOCR {

    companion object {
        @androidx.annotation.OptIn(ExperimentalGetImage::class)
        fun analyzeImage(imageProxy: ImageProxy, onSuccess: (Text) -> Unit) {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                val result = recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        Log.d("INFO", "analyze: Success")
                        Log.d("INFO", visionText.text)
                        onSuccess(visionText)
                        Log.d("INFO", "Callback complete")
                    }
                    .addOnFailureListener { e ->
                        Log.d("WARNING", "analyze: Failure")
                        Log.d("WARNING", e.toString())
                    }
            }
        }
    }
}