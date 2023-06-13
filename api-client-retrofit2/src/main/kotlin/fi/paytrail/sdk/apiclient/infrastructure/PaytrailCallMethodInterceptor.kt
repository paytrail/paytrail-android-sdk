package fi.paytrail.sdk.apiclient.infrastructure

class PaytrailCallMethodInterceptor : HeaderInjectingInterceptor(
    "checkout-method",
    { it.method }
)