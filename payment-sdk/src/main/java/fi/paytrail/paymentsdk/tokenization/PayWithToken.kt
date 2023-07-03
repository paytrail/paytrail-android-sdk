package fi.paytrail.paymentsdk.tokenization

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.paytrail.paymentsdk.LoadingIndicator
import fi.paytrail.paymentsdk.PaytrailWebView
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.sdk.apiclient.models.PaymentRequest

enum class TokenPaymentChargeType {
    AUTH_HOLD,
    CHARGE,
}

enum class TokenPaymentType(s: String) {
    MIT("mit"),
    CIT("cit"),
}

@Composable
fun PayWithTokenizationId(
    modifier: Modifier = Modifier,
    paymentRequest: PaymentRequest,
    tokenizationId: String,
    onPaymentStateChanged: (PaytrailPaymentState) -> Unit,
    paymentType: TokenPaymentType = TokenPaymentType.CIT,
    chargeType: TokenPaymentChargeType = TokenPaymentChargeType.CHARGE,
) {
    val viewModel: PayWithTokenViewModel = viewModel(
        factory = PayWithTokenViewModelFactory(
            tokenizationId = tokenizationId,
            token = null,
            paymentRequest = paymentRequest,
            paymentType = paymentType,
            chargeType = chargeType,
        ),
    )

    PayWithToken(
        modifier = modifier,
        viewModel = viewModel,
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
    onPaymentStateChanged: (PaytrailPaymentState) -> Unit,
) {
    val viewModel: PayWithTokenViewModel = viewModel(
        factory = PayWithTokenViewModelFactory(
            tokenizationId = null,
            token = token,
            paymentRequest = paymentRequest,
            paymentType = paymentType,
            chargeType = chargeType,
        ),
    )

    PayWithToken(
        modifier = modifier,
        viewModel = viewModel,
        onPaymentStateChanged = onPaymentStateChanged,
    )
}

@Composable
fun PayWithToken(
    modifier: Modifier = Modifier,
    viewModel: PayWithTokenViewModel,
    onPaymentStateChanged: (PaytrailPaymentState) -> Unit,
) {
    val paymentStatus =
        viewModel.paymentState.collectAsState(
            initial = PaytrailPaymentState(PaytrailPaymentState.State.PAYMENT_IN_PROGRESS),
        ).value

    LaunchedEffect(paymentStatus) {
        onPaymentStateChanged(paymentStatus)
    }

    val url = viewModel.payment3DSRedirectUrl.collectAsState(initial = null).value

    Surface(modifier) {
        if (url != null) {
            PaytrailWebView(
                modifier = Modifier.fillMaxSize(),
                url = url,
                redirectUrls = viewModel.paymentRequest.redirectUrls,
                onFinalRedirect = viewModel::finalRedirectReceived,
                onError = viewModel::errorReceived,
            )
        } else {
            LoadingIndicator()
        }
    }
}
