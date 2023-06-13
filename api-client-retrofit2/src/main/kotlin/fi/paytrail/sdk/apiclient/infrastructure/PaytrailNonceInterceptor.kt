package fi.paytrail.sdk.apiclient.infrastructure

import java.util.UUID

class PaytrailNonceInterceptor : HeaderInjectingInterceptor(
    "checkout-nonce",
    { UUID.randomUUID().toString() }
)