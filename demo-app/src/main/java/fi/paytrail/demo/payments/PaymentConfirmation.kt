package fi.paytrail.demo.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.paytrail.demo.R
import fi.paytrail.demo.ui.theme.FilledButton
import fi.paytrail.demo.ui.theme.PaytrailTypography
import fi.paytrail.paymentsdk.model.PaytrailPaymentState

@Composable
fun PaymentConfirmation(
    modifier: Modifier = Modifier,
    paymentState: PaytrailPaymentState?,
    onClick: () -> Unit = {},
) {
    var resultToShow by remember { mutableStateOf(paymentState) }
    if (paymentState != null) resultToShow = paymentState

    val status = resultToShow?.state
    // Unused for this demo purpose
    val transactionId = resultToShow?.finalRedirectRequest?.transactionId
        ?: resultToShow?.tokenPaymentResponse?.transactionId?.toString()
    val errorMessage = resultToShow?.exception?.toString()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (status) {
            PaytrailPaymentState.State.PAYMENT_OK -> {
                PaymentConfirmationSuccess(onClick)
            }

            PaytrailPaymentState.State.PAYMENT_FAIL, PaytrailPaymentState.State.PAYMENT_ERROR, PaytrailPaymentState.State.PAYMENT_CANCELED -> {
                PaymentConfirmationFailed(failureMessage = errorMessage, onClick)
            }

            else -> {}
        }
    }
}

@Composable
fun PaymentConfirmationSuccess(onClick: () -> Unit) {
    Text(
        text = stringResource(id = R.string.payment_confirmation_success_title),
        style = PaytrailTypography.titleMedium,
    )
    Spacer(modifier = Modifier.height(43.dp))
    Column(
        modifier = Modifier
            .background(Color.White)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(7.dp),
            )
            .fillMaxWidth()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(id = R.string.payment_confirmation_success),
            style = PaytrailTypography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        FilledButton(text = stringResource(id = R.string.payment_confirmation_success_button)) {
            onClick()
        }
    }
}

@Composable
fun PaymentConfirmationFailed(failureMessage: String?, onClick: () -> Unit) {
    Text(
        text = stringResource(id = R.string.payment_confirmation_failed_title),
        style = PaytrailTypography.titleMedium,
    )
    Spacer(modifier = Modifier.height(43.dp))
    Column(
        modifier = Modifier
            .background(Color.White)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(7.dp),
            ).fillMaxWidth().padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = failureMessage ?: stringResource(id = R.string.payment_confirmation_error),
            style = PaytrailTypography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        FilledButton(text = stringResource(id = R.string.payment_confirmation_failed_button)) {
            onClick()
        }
    }
}

@Preview
@Composable
fun PaymentConfirmationScreen() {
    PaymentConfirmation(paymentState = null)
}
