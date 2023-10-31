package fi.paytrail.paymentsdk.tokenization

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.paytrail.paymentsdk.LoadingIndicator
import fi.paytrail.paymentsdk.PaymentStateChangeListener
import fi.paytrail.paymentsdk.PaytrailWebView
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailApiClient
import fi.paytrail.sdk.apiclient.models.PaymentRequest

/**
 * Represents the type of payment charge when dealing with tokens.
 */
enum class TokenPaymentChargeType {
    AUTH_HOLD,
    CHARGE,
}
/**
 * Represents the type of token payment in compliance with the European PSD2 directive.
 */
enum class TokenPaymentType {
    MIT,
    CIT,
}
/**
 * A Composable function for processing payments using a saved tokenization ID.
 *
 * This function facilitates the payment process using a previously obtained tokenization ID.
 * This is particularly useful for executing transactions without repeatedly prompting the user for card details.
 *
 * @param modifier The modifier to be applied to the Composable.
 * @param paymentRequest Contains details of the payment like amount, reference, etc.
 * @param tokenizationId The previously saved ID from the card tokenization process.
 * @param paymentType Type of token payment - can be either CIT (default) or MIT.
 * @param chargeType Type of payment charge - can either be CHARGE (default) or AUTH_HOLD.
 * @param onTokenAvailable Callback invoked when a token becomes available.
 * @param onPaymentStateChanged Callback invoked when there's any change in the payment state.
 * @param merchantAccount Contains merchant's account details.
 * @param apiClient Client for connecting to the Paytrail API.
 */
@Composable
fun PayWithTokenizationId(
    modifier: Modifier = Modifier,
    paymentRequest: PaymentRequest,
    tokenizationId: String,
    paymentType: TokenPaymentType = TokenPaymentType.CIT,
    chargeType: TokenPaymentChargeType = TokenPaymentChargeType.CHARGE,
    onTokenAvailable: (String) -> Unit,
    onPaymentStateChanged: PaymentStateChangeListener,
    merchantAccount: MerchantAccount,
    apiClient: PaytrailApiClient = PaytrailApiClient(merchantAccount = merchantAccount),
) {
    val viewModel: PayWithTokenViewModel = viewModel(
        factory = PayWithTokenViewModelFactory(
            tokenizationId = tokenizationId,
            token = null,
            paymentRequest = paymentRequest,
            paymentType = paymentType,
            chargeType = chargeType,
            apiClient = apiClient,
        ),
    )

    LaunchedEffect(tokenizationId, paymentRequest) {
        viewModel.paymentToken.collect { if (it.isSuccess) onTokenAvailable(it.value!!) }
    }

    PayWithToken(
        modifier = modifier,
        viewModel = viewModel,
        merchantAccount = merchantAccount,
        onPaymentStateChanged = onPaymentStateChanged,
    )
}

@Composable
fun PayWithToken(
    modifier: Modifier = Modifier,
    paymentRequest: PaymentRequest,
    token: String,
    paymentType: TokenPaymentType = TokenPaymentType.CIT,
    chargeType: TokenPaymentChargeType = TokenPaymentChargeType.CHARGE,
    onPaymentStateChanged: PaymentStateChangeListener,
    merchantAccount: MerchantAccount,
    apiClient: PaytrailApiClient = PaytrailApiClient(merchantAccount = merchantAccount),
) {
    val viewModel: PayWithTokenViewModel = viewModel(
        factory = PayWithTokenViewModelFactory(
            tokenizationId = null,
            token = token,
            paymentRequest = paymentRequest,
            paymentType = paymentType,
            chargeType = chargeType,
            apiClient = apiClient,
        ),
    )

    PayWithToken(
        modifier = modifier,
        viewModel = viewModel,
        merchantAccount = merchantAccount,
        onPaymentStateChanged = onPaymentStateChanged,
    )
}

@Composable
internal fun PayWithToken(
    modifier: Modifier = Modifier,
    viewModel: PayWithTokenViewModel,
    merchantAccount: MerchantAccount,
    onPaymentStateChanged: PaymentStateChangeListener,
) {
    val paymentStatus =
        viewModel.paymentState.collectAsState(
            initial = PaytrailPaymentState(PaytrailPaymentState.State.PAYMENT_IN_PROGRESS),
        ).value

    LaunchedEffect(paymentStatus) {
        onPaymentStateChanged.onPaymentStateChanged(paymentStatus)
    }

    val url = viewModel.payment3DSRedirectUrl.collectAsState(initial = null).value

    Surface(modifier) {
        if (url != null) {
            PaytrailWebView(
                modifier = Modifier.fillMaxSize(),
                url = url,
                redirectUrls = viewModel.paymentRequest.redirectUrls,
                onFinalRedirect = viewModel::finalRedirectReceived,
                onError = viewModel::webviewErrorReceived,
                signatureVerificationSecret = merchantAccount.secret,
            )
        } else {
            LoadingIndicator()
        }
    }
}
