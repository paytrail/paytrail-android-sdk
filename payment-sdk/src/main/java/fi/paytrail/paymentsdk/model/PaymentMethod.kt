package fi.paytrail.paymentsdk.model

import fi.paytrail.sdk.apiclient.models.PaymentMethodProvider

data class PaymentMethod(
    val provider: PaymentMethodProvider,
) {
    val id: String = provider.id
    val name: String = provider.name
    val svg: String = provider.svg
    val formParameters: List<Pair<String, String>> by lazy {
        provider.parameters.map { it.name to it.value }
    }
}
