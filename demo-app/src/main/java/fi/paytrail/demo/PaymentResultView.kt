package fi.paytrail.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_ERROR
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_FAIL
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_OK
import java.io.IOException

/**
 * The 'Compose' [PaymentResultView] provides an alternative to the conventional [PaymentConfirmation] style.
 * It can be utilized to display an alert message based on the payment's outcome: either successful or failed.
 *
 * You will have to include in your parent surface view for example, so the state is visible across your entire screen
 *
 * ```kotlin
 * var currentState = paymentState?.second
 * AnimatedVisibility(visible = shouldShowStatus(currentState)) {
 *     PaymentResultView(
 *         paymentState = currentState,
 *         onClick = {
 *             navController.navigate(
 *                 NAV_PAYMENT_DETAILS.replace(
 *                     "{$NAV_ARG_PAYMENT_ID}",
 *                     paymentState?.first.toString()
 *                 )
 *             )
 *         },
 *         onHide = { paymentState = null }
 *     )
 * }
 * ```
 */
@Composable
fun PaymentResultView(
    paymentState: PaytrailPaymentState?,
    onHide: () -> Unit,
    onClick: () -> Unit,
) {
    var resultToShow by remember { mutableStateOf(paymentState) }
    if (paymentState != null) resultToShow = paymentState

    val status = resultToShow?.state
    val transactionId = resultToShow?.finalRedirectRequest?.transactionId
        ?: resultToShow?.tokenPaymentResponse?.transactionId?.toString()
    val errorMessage = resultToShow?.exception?.toString()

    PaymentResultView(status, transactionId, errorMessage, onClick, onHide)
}

@Composable
private fun PaymentResultView(
    status: PaytrailPaymentState.State?,
    transactionId: String?,
    errorMessage: String?,
    onClick: () -> Unit = {},
    onHide: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when (status) {
                    PAYMENT_OK -> Color.Green
                    PAYMENT_FAIL, PAYMENT_ERROR -> Color.Red
                    else -> Color.Gray
                },
            ),
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .weight(1f)
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(
                    R.string.payment_status_view_status,
                    status ?: "",
                ),
            )
            if (transactionId != null) {
                Text(
                    text = stringResource(
                        R.string.payment_status_view_transaction_id,
                        transactionId,
                    ),
                    fontSize = 10.sp,
                )
            }
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    fontSize = 10.sp,
                    maxLines = 4,
                )
            }
        }

        IconButton(
            modifier = Modifier.align(CenterVertically),
            onClick = onHide,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Close",
            )
        }
    }
}

@Preview
@Composable
fun PreviewPaymentResultViewSuccess() {
    PaymentResultView(
        status = PAYMENT_OK,
        transactionId = "841fe3cc-8d82-4f2e-ae67-fc1e10be10a2",
        errorMessage = null,
    )
}

@Preview
@Composable
fun PreviewPaymentResultViewFailure() {
    PaymentResultView(
        status = PAYMENT_FAIL,
        transactionId = "841fe3cc-8d82-4f2e-ae67-fc1e10be10a2",
        errorMessage = null,
    )
}

@Preview
@Composable
fun PreviewPaymentResultViewError() {
    PaymentResultView(
        status = PAYMENT_ERROR,
        transactionId = null,
        errorMessage = IOException("Preview exception").toString(),
    )
}
