package fi.paytrail.sdk.apiclient.infrastructure

class PaytrailAccountIdInjector(accountIdProvider: () -> Int) : HeaderInjectingInterceptor(
    "checkout-account",
    { accountIdProvider.invoke().toString() },
)
