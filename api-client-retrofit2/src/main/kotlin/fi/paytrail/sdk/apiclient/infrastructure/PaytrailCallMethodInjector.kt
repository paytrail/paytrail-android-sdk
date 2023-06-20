package fi.paytrail.sdk.apiclient.infrastructure

class PaytrailCallMethodInjector : HeaderInjectingInterceptor(
    "checkout-method",
    { it.method },
)
