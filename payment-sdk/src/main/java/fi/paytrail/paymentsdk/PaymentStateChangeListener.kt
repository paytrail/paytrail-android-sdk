package fi.paytrail.paymentsdk

import fi.paytrail.paymentsdk.model.PaytrailPaymentState

interface PaymentStateChangeListener {
    fun onPaymentStateChanged(state: PaytrailPaymentState)
}
