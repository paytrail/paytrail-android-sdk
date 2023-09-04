package fi.paytrail.paymentsdk

import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class PaytrailLaunchPaymentMethodTest : BasePaytrailUiTest() {
    @Test
    fun testLaunchTestPayment() {
        val callbacks: MutableList<PaytrailPaymentState> = mutableListOf()

        val callback: (PaytrailPaymentState) -> Unit = {
            callbacks.add(it)
        }

        with(composeTestRule) {
            startPaymentMethodSelection(
                paymentRequest = simplePaymentRequest,
                port = mockApiServer.port,
                callback = callback,
            )
            waitUntilPaymentProvidersShown()
            clickPaymentProvider("Test Provider")

            waitUntil {
                callbacks.any { it.state == PaytrailPaymentState.State.PAYMENT_OK }
            }
        }

        // Verify the payment is launched to correct URL
        with(apiResponses.requests.last { it.path == "/test-payment" }) {
            method shouldBeEqualTo "POST"
            body.readUtf8() shouldBeEqualTo "test-parameter-1=test-value-1&test-parameter-2=test-value-2&test-parameter-3=test-value-3"
        }

        callbacks.filter { it.state == PaytrailPaymentState.State.PAYMENT_OK }.size shouldBe 1
        with(callbacks.first { it.state == PaytrailPaymentState.State.PAYMENT_OK }) {
            apiErrorResponse shouldBe null
            exception shouldBe null
            tokenPaymentResponse shouldBe null
            finalRedirectRequest!!.account shouldBeEqualTo defaultMerchantAccount.id
            finalRedirectRequest!!.provider shouldBeEqualTo "test-provider"
        }
    }
}
