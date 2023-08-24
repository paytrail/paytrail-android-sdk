package fi.paytrail.paymentsdk

import androidx.compose.ui.test.junit4.createComposeRule
import fi.paytrail.paymentsdk.test.server.MockApiServer
import fi.paytrail.paymentsdk.test.server.RequestMethod.POST
import fi.paytrail.paymentsdk.test.server.TapeAssetDispatcher
import fi.paytrail.paymentsdk.test.server.withBody
import fi.paytrail.paymentsdk.test.server.withHeader
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailHmacCalculator
import fi.paytrail.sdk.apiclient.models.Callbacks
import fi.paytrail.sdk.apiclient.models.Currency
import fi.paytrail.sdk.apiclient.models.Customer
import fi.paytrail.sdk.apiclient.models.Language
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import org.junit.Before
import org.junit.Rule
import java.net.URL

abstract class BasePaytrailUiTest {
    val simplePaymentRequest
        get() = PaymentRequest(
            stamp = "test",
            reference = "test",
            amount = 100,
            currency = Currency.EUR,
            language = Language.EN,
            customer = Customer(
                email = "android-test-customer@paytrail.fi",
            ),
            redirectUrls = Callbacks(
                success = "http://localhost:${mockApiServer.port}/test-payment-success",
                cancel = "http://localhost:${mockApiServer.port}/test-payment-cancel",
            ),
        )

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val mockApiServer: MockApiServer = MockApiServer()

    val apiResponses
        get() = (mockApiServer.mockWebServer.dispatcher as TapeAssetDispatcher)

    @Before
    fun foo() {
        apiResponses {

            apiResponses {
                POST requestOf "/payments" serves "payments-response-default"

                // Launching test payment provider immediately redirects to success URL by default.
                POST requestOf "/test-payment" serves (302 withBody "" withHeader ("Location" to paymentSuccessfulRedirectUrl()))

                "port" bodyTokenReplacedBy { mockApiServer.port.toString() }
                "signature" globalHeader ::responseSignature
            }
        }
    }

    /** Created URL for redirecting webview during payment flow to defaultsuccess URL */
    protected fun paymentSuccessfulRedirectUrl(): String {
        val paymentSuccessfulUnsigned =
            "http://localhost:${mockApiServer.port}/test-payment-success?foo=bar" +
                "&checkout-algorithm=sha512" +
                "&checkout-account=${defaultMerchantAccount.id}" +
                "&checkout-amount=123" +
                "&checkout-stamp=123" +
                "&checkout-reference=123" +
                "&checkout-transaction-id=123" +
                "&checkout-status=ok" +
                "&checkout-provider=test-provider"

        return "$paymentSuccessfulUnsigned&signature=" + PaytrailHmacCalculator.calculateHmac(
            URL(paymentSuccessfulUnsigned),
            defaultMerchantAccount.secret,
        )
    }
}

fun responseSignature(
    headers: Map<String, String>,
    body: String,
    algorithm: String = "sha512",
    key: String = "SAIPPUAKAUPPIAS",
) = PaytrailHmacCalculator.getCalculator(algorithm).calculateHmac(
    params = headers.toList(),
    body = body,
    key = key,
)
