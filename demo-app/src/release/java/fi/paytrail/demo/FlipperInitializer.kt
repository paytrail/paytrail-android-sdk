import android.content.Context
import okhttp3.OkHttpClient

object FlipperInitializer {
    fun initialize(context: Context, okHttpClientBuilder: OkHttpClient.Builder) {
        // No flipper in release flavor.
    }
}
