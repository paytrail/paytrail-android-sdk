import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import okhttp3.OkHttpClient

object FlipperInitializer {
    fun initialize(context: Context, okHttpClientBuilder: OkHttpClient.Builder) {
        SoLoader.init(context, false)
        if (FlipperUtils.shouldEnableFlipper(context)) {
            val client = AndroidFlipperClient.getInstance(context)
            client.addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
            client.addPlugin(CrashReporterPlugin.getInstance())

            val networkPlugin = NetworkFlipperPlugin()
            client.addPlugin(networkPlugin)
            okHttpClientBuilder.addNetworkInterceptor(FlipperOkhttpInterceptor(networkPlugin))

            client.start()
        }
    }
}
