package fi.paytrail.paymentsdk

import android.util.Log
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailApiClient
import fi.paytrail.sdk.apiclient.models.PaymentRequest

val defaultMerchantAccount = MerchantAccount(id = 375917, secret = "SAIPPUAKAUPPIAS")

fun ComposeContentTestRule.startPaymentMethodSelection(
    paymentRequest: PaymentRequest,
    port: Int,
    merchantAccount: MerchantAccount = defaultMerchantAccount,
    callback: (PaytrailPaymentState) -> Unit = { },
) {
    setContent {
        PaytrailPayment(
            paymentRequest = paymentRequest,
            onPaymentStateChanged = callback,
            apiClient = PaytrailApiClient(
                baseUrl = "http://localhost:$port",
                merchantAccount = merchantAccount,
            ),
            merchantAccount = merchantAccount,
        )
    }
}

fun ComposeContentTestRule.assertGroupOrderingIs(expectedGroupOrdering: List<String>) {
    try {
        val listingNode = onNode(hasTestTag("PaymentProvidersListing"), useUnmergedTree = true)
        val groupNodes = listingNode
            .onChildren()
            .filter(hasTestTag("PaymentProviderGroup"))

        groupNodes.assertCountEquals(expectedGroupOrdering.size)

        expectedGroupOrdering.forEachIndexed { index, expectedHeader ->
            val node = groupNodes[index]
            node.onChildren()
                .assertAny(hasTestTag("PaymentProviderGroupHeader").and(hasText(expectedHeader)))
        }
    } catch (t: Throwable) {
        onRoot(useUnmergedTree = true).printToLog("PaymentTest")
        throw t
    }
}

fun ComposeContentTestRule.assertGroupProviderOrdering(
    groupHeader: String,
    expectedProviders: List<String>,
) {
    try {
        val listingNode = onNode(hasTestTag("PaymentProvidersListing"), useUnmergedTree = true)
        val groupNode = listingNode
            .onChildren()
            .filterToOne(
                hasTestTag("PaymentProviderGroup").and(
                    hasAnyChild(
                        hasTestTag("PaymentProviderGroupHeader").and(hasText(groupHeader)),
                    ),
                ),
            )

        val providerNodes = groupNode.onChildren().filter(hasTestTag("PaymentProvider"))

        expectedProviders.forEachIndexed { index, expectedProvider ->
            val node = providerNodes[index]
            node.onChildren().assertAny(hasContentDescription(expectedProvider))

        }

    } catch (t: Throwable) {
        onRoot(useUnmergedTree = true).printToLog("PaymentTest")
        throw t
    }
}

@OptIn(ExperimentalTestApi::class)
fun ComposeContentTestRule.waitUntilPaymentProvidersShown() {
    try {
        waitUntilExactlyOneExists(hasTestTag("PaymentProvidersListing"), 5000)
    } catch (t: Throwable) {
        onRoot().printToLog("PaymentTest")
        throw t
    }
}

fun ComposeContentTestRule.clickPaymentProvider(name: String) {
    try {
        onNode(hasTestTag("PaymentProvider").and(hasContentDescription(name))).performClick()
    } catch (t: Throwable) {
        onRoot(useUnmergedTree = true).printToLog("PaymentTest")
        throw t
    }
}
