package com.example.expenditurelogger.utils

import android.content.Context
import android.util.Log
import com.example.expenditurelogger.shared.Transaction
import com.google.gson.Gson
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UploadDataProviders
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object NetworkWorker {

    private var myBuilder: CronetEngine.Builder? = null
    private var cronetEngine: CronetEngine? = null
    private var requestBuilder: UrlRequest.Builder? = null

    private var backendURL: String = "https://expenditure-application.fly.dev"

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private val gson = Gson()

    fun init(context: Context) {
        myBuilder = CronetEngine.Builder(context)
        cronetEngine = myBuilder!!.build()

        requestBuilder = cronetEngine?.newUrlRequestBuilder(
            backendURL + "/transactions",
            RequestCallback(),
            executor
        )
    }

    fun updateBackendUrl(newUrl: String) {
        backendURL = newUrl

        requestBuilder = cronetEngine?.newUrlRequestBuilder(
            backendURL + "/transactions",
            RequestCallback(),
            executor
        )
    }

    class RequestCallback : UrlRequest.Callback() {
        override fun onRedirectReceived(
            request: UrlRequest?,
            info: UrlResponseInfo?,
            newLocationUrl: String?
        ) {

        }

        override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {

        }

        override fun onReadCompleted(
            request: UrlRequest?,
            info: UrlResponseInfo?,
            byteBuffer: ByteBuffer?
        ) {

        }

        override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {

        }

        override fun onFailed(
            request: UrlRequest?,
            info: UrlResponseInfo?,
            error: CronetException?
        ) {

        }

    }

    fun postTranscation(requestData: Transaction) {
        requestBuilder?.addHeader("Content-Type", "application/json")
        requestBuilder?.setHttpMethod("POST")
        requestBuilder?.setUploadDataProvider(
            UploadDataProviders.create(
                gson.toJson(requestData).toByteArray()
            ), executor
        )
        val request: UrlRequest? = requestBuilder?.build()

        Log.d("HTTP POST", "postTranscation: ${requestData}: ${backendURL} ${request.toString()}")

        request?.start()
        Log.d("HTTP POST", "Finished")
    }

}