package fi.paytrail.paymentsdk

import okhttp3.OkHttpClient

object PaytrailBaseOkHttpClient {
    var baseClient: OkHttpClient? = null
        internal set

    fun install(baseClient: OkHttpClient) {
        PaytrailBaseOkHttpClient.baseClient = baseClient
    }

}