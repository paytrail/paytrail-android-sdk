package fi.paytrail.sdk.apiclient.infrastructure

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

class PaytrailAccountIdInterceptor(accountIdProvider: () -> Int) : HeaderInjectingInterceptor(
    "checkout-account",
    { accountIdProvider.invoke().toString() }
)