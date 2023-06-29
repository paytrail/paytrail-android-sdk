package fi.paytrail.sdk.apiclient

import okhttp3.OkHttpClient

/**
 * Helper to provide a default [OkHttpClient] instance which
 * Paytrail SDK will use to build the client for calling API
 * endpoints with [ApiClient]
 */
object PaytrailBaseOkHttpClient {
    // TODO: Move this functionality directly into ApiClient?
    var baseClient: OkHttpClient? = null
        internal set

    fun install(baseClient: OkHttpClient) {
        PaytrailBaseOkHttpClient.baseClient = baseClient
    }
}
