@file:OptIn(ExperimentalTestApi::class)

package fi.paytrail.paymentsdk

import androidx.compose.ui.test.ExperimentalTestApi
import fi.paytrail.paymentsdk.test.server.RequestMethod.POST
import org.junit.Test


const val CREDIT = "Invoice and instalment payment methods"
const val CREDIT_CARD = "Card payment methods"
const val MOBILE = "Mobile payment methods"
const val BANK = "Bank payment methods"

val defaultGroupOrdering = listOf(CREDIT, MOBILE, CREDIT_CARD, BANK)
val defaultCreditCardOrdering = listOf("Visa", "Visa Electron", "Mastercard", "American Express")

class PaytrailPaymentProviderOrderingTest : BasePaytrailUiTest() {

    @Test
    fun testDefaultOrder() {
        with(composeTestRule) {
            startPaymentMethodSelection(simplePaymentRequest, mockApiServer.port)
            waitUntilPaymentProvidersShown()
            assertGroupOrderingIs(defaultGroupOrdering)
            assertGroupProviderOrdering(
                CREDIT_CARD,
                defaultCreditCardOrdering,
            )
        }
    }

    @Test
    fun testDifferentGroupOrder() {
        // Compared to default, Groups have been reordered to:
        //    1. Bank
        //    2. Mobile
        //    3. Credit card
        //    4. Credit

        apiResponses {
            POST requestOf "/payments" serves "payments-response-different-group-ordering"
        }

        with(composeTestRule) {
            startPaymentMethodSelection(simplePaymentRequest, mockApiServer.port)
            waitUntilPaymentProvidersShown()
            assertGroupOrderingIs(listOf(BANK, MOBILE, CREDIT_CARD, CREDIT))
            assertGroupProviderOrdering(
                CREDIT_CARD,
                defaultCreditCardOrdering,
            )

        }
    }

    @Test
    fun testDifferentCreditCardProviderOrder() {
        // Compared to default, Credit cards have been reordered to:
        //    1. Visa
        //    2. Amex
        //    3. Mastercard
        //    4. Visa Electron

        apiResponses {
            POST requestOf "/payments" serves "payments-response-different-provider-ordering"
        }

        with(composeTestRule) {
            startPaymentMethodSelection(simplePaymentRequest, mockApiServer.port)
            waitUntilPaymentProvidersShown()
            assertGroupOrderingIs(defaultGroupOrdering)
            assertGroupProviderOrdering(
                CREDIT_CARD,
                listOf("Visa", "American Express", "Mastercard", "Visa Electron"),
            )

        }
    }

}
