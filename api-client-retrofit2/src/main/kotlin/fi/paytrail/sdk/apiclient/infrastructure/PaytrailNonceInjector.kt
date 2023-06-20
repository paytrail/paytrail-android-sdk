package fi.paytrail.sdk.apiclient.infrastructure

import java.util.UUID

class PaytrailNonceInjector : HeaderInjectingInterceptor(
    "checkout-nonce",
    { UUID.randomUUID().toString() },
)
