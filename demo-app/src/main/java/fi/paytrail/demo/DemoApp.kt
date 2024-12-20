package fi.paytrail.demo

import FlipperInitializer
import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import android.webkit.WebView
import dagger.hilt.android.HiltAndroidApp
import fi.paytrail.paymentsdk.PaytrailLogger
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.PaytrailBaseOkHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY

val SAMPLE_MERCHANT_ACCOUNT = MerchantAccount(id = 375917, secret = "SAIPPUAKAUPPIAS")

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

        // Provide Paytrail SDK with OkHttp client set up with logging interceptors.
        // SDK will use this client as basis for the OkHttpClient it uses to call API
        // endpoints.
        PaytrailBaseOkHttpClient.install(okHttpClientBuilder.build())
        // Enabling logs
        PaytrailLogger.isLoggingEnabled = true
        // Allow debugging webview content for debug builds
        @SuppressLint("ObsoleteSdkInt")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }
}
