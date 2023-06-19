package fi.paytrail.demo

import FlipperInitializer
import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import android.webkit.WebView
import dagger.hilt.android.HiltAndroidApp
import fi.paytrail.paymentsdk.PaytrailBaseOkHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY

@HiltAndroidApp
class DemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val okHttpClientBuilder = OkHttpClient.Builder()

        // Set up HTTP logging
        val httpLogger = HttpLoggingInterceptor { Log.i("OkHttp", it) }
        httpLogger.level = if (BuildConfig.DEBUG) BODY else BASIC
        okHttpClientBuilder.addInterceptor(httpLogger)

        // Initialize Flipper
        FlipperInitializer.initialize(applicationContext, okHttpClientBuilder)

        // Provide Paytrail SDK with OkHttp builder set up with logging plugins.
        // SDK will set up a new client based on this client, if it has been installed.
        PaytrailBaseOkHttpClient.install(okHttpClientBuilder.build())

        // Allow debugging webview content for debug builds
        @SuppressLint("ObsoleteSdkInt")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }
}
