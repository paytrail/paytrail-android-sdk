package fi.paytrail.paymentsdk.tokenization.model

import fi.paytrail.sdk.apiclient.models.Callbacks
import fi.paytrail.sdk.apiclient.models.Language

data class AddCardRequest(
    val redirectUrls: Callbacks,
    val callbackUrls: Callbacks? = null,
    val language: Language? = null,
)
