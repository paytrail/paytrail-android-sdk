package fi.paytrail.demo;

import android.app.Application
import android.util.Log
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import dagger.hilt.android.HiltAndroidApp
import fi.paytrail.paymentsdk.PaytrailBaseOkHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


@HiltAndroidApp
class DemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        SoLoader.init(this, false)
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(CrashReporterPlugin.getInstance())

            val networkFlipperPlugin = NetworkFlipperPlugin()
            client.addPlugin(networkFlipperPlugin)

            // Set up Paytrail API with OkHttp client, which is able to log network requests.
            val httpLogger = HttpLoggingInterceptor { Log.i("OkHttp", it) }
            httpLogger.level = HttpLoggingInterceptor.Level.BODY
            PaytrailBaseOkHttpClient.install(
                OkHttpClient.Builder()
                    .addNetworkInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
                    .addInterceptor(httpLogger)
                    .build()
            )

            client.start()
        }
    }

}
