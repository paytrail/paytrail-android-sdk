package fi.paytrail.paymentsdk.model

import fi.paytrail.sdk.apiclient.models.PaymentMethodGroupData

data class PaymentMethodGroup(
    val paymentMethodGroup: PaymentMethodGroupData,
    val paymentMethods: List<PaymentMethod>,
) {
    val id = paymentMethodGroup.id
    val name = paymentMethodGroup.name
    val svg = paymentMethodGroup.svg
}
