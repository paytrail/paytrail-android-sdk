package fi.paytrail.paymentsdk.tokenization

import fi.paytrail.sdk.apiclient.models.PaymentRequest
import fi.paytrail.sdk.apiclient.models.TokenPaymentRequest

fun PaymentRequest.asTokenizedPaymentRequest(token: String): TokenPaymentRequest =
    TokenPaymentRequest(
        stamp = stamp,
        reference = reference,
        amount = amount,
        currency = currency,
        language = language,
        items = items,
        customer = customer,
        redirectUrls = redirectUrls,
        token = token,
        orderId = orderId,
        deliveryAddress = deliveryAddress,
        invoicingAddress = invoicingAddress,
        callbackUrls = callbackUrls,
        callbackDelay = callbackDelay,
    )
