package fi.paytrail.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.paytrail.paymentsdk.PaytrailPaymentResult

@Composable
fun PaymentResultView(paymentResult: PaytrailPaymentResult?, onHide: () -> Unit) {
    var resultToShow by remember { mutableStateOf(paymentResult) }
    if (paymentResult != null) resultToShow = paymentResult

    val status = resultToShow?.status
    val transactionId = resultToShow?.transactionId

    PaymentResultView(status, transactionId, onHide)
}

@Composable
private fun PaymentResultView(
    status: PaytrailPaymentResult.Status?,
    transactionId: String?,
    onHide: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when (status) {
                    PaytrailPaymentResult.Status.Ok -> Color.Green
                    PaytrailPaymentResult.Status.Fail -> Color.Red
                    else -> Color.Gray
                },
            )
            .padding(16.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    R.string.payment_status_view_status,
                    status ?: "",
                ),
            )
            Text(
                text = stringResource(
                    R.string.payment_status_view_transaction_id,
                    transactionId ?: "",
                ),
                fontSize = 10.sp,
            )
        }

        Icon(
            modifier = Modifier
                .clickable { onHide() }
                .padding(start = 16.dp),
            painter = painterResource(id = R.drawable.close),
            contentDescription = "Close",
        )
    }
}

@Preview
@Composable
fun PreviewPaymentResultViewSuccess() {
    PaymentResultView(
        status = PaytrailPaymentResult.Status.Ok,
        transactionId = "841fe3cc-8d82-4f2e-ae67-fc1e10be10a2",
        onHide = {},
    )
}

@Preview
@Composable
fun PreviewPaymentResultViewFailure() {
    PaymentResultView(
        status = PaytrailPaymentResult.Status.Fail,
        transactionId = "841fe3cc-8d82-4f2e-ae67-fc1e10be10a2",
        onHide = {},
    )
}
