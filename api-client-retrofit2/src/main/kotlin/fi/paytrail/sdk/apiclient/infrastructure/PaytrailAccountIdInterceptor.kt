package fi.paytrail.sdk.apiclient.infrastructure

class PaytrailAccountIdInterceptor(accountIdProvider: () -> Int) : HeaderInjectingInterceptor(
    "checkout-account",
    { accountIdProvider.invoke().toString() },
)
