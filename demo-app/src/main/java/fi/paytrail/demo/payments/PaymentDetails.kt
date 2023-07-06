package fi.paytrail.demo.payments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fi.paytrail.paymentsdk.RequestStatus
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.sdk.apiclient.models.Payment
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import fi.paytrail.sdk.apiclient.models.TokenPaymentResponse

@Composable
fun PaymentDetails(
    viewModel: PaymentDetailsViewModel,
    modifier: Modifier,
) {
    val localPaymentData = viewModel.paymentData.collectAsState(initial = null).value
    val paymentDetailsRequest =
        viewModel.paymentDetails.collectAsState(initial = RequestStatus.loading()).value

    Surface(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = 48.dp, start = 16.dp, end = 16.dp),
    ) {
        if (localPaymentData != null) {
            val paymentRequest = localPaymentData.paymentRequest

            Column {
                Section("Payment details:") {
                    PaymentRequestDetails(paymentDetailsRequest, viewModel)
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                Section("PaymentRequest:") {
                    PaymentRequestDetails(paymentRequest)
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                Section("Local state:") {
                    LocalPaymentState(localPaymentData.state)
                }
            }
        } else {
            Text("No payment data found with ID ${viewModel.paymentId}")
        }
    }
}

@Composable
private fun PaymentRequestDetails(
    paymentDetailsRequest: RequestStatus<Payment>,
    viewModel: PaymentDetailsViewModel,
) {
    val authHoldActionState = viewModel.authHoldActionState.collectAsState(null).value
    when {
        paymentDetailsRequest.isSuccess -> PaymentDetails(
            payment = paymentDetailsRequest.value!!,
            onCommit = {
                viewModel.commit()
            },
            onRevert = viewModel::revert,
            authHoldActionState = authHoldActionState,
        )

        paymentDetailsRequest.isError -> PaymentDetailsFailed(paymentDetailsRequest)
        paymentDetailsRequest.isLoading -> PaymentDetailsLoading()
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Text(title, style = typography.titleLarge)
    Spacer(modifier = Modifier.height(4.dp))
    content()
}

@Composable
fun LocalPaymentState(state: PaytrailPaymentState?) {
    if (state != null) {
        Text(state.toString())
    } else {
        Text("No state info available")
    }
}

@Composable
fun PaymentRequestDetails(paymentRequest: PaymentRequest?) {
    if (paymentRequest != null) {
        Text(paymentRequest.toString())
    } else {
        Text("No PaymentRequest available")
    }
}

@Composable
fun PaymentDetails(
    payment: Payment,
    onCommit: () -> Unit,
    onRevert: () -> Unit,
    authHoldActionState: RequestStatus<TokenPaymentResponse>?,
) {
    Column {
        Text(payment.toString())
        if (payment.status == Payment.Status.AuthorizationHold) {
            AuthorizationHoldButtons(
                authRequestInProgress = authHoldActionState?.isLoading ?: false,
                onCommit = onCommit,
                onRevert = onRevert,
            )
        }
        if (authHoldActionState?.isError == true) {
            AuthorizationHoldError(authHoldActionState)
        }
    }
}

@Composable
fun AuthorizationHoldError(authHoldActionState: RequestStatus<TokenPaymentResponse>) {
    Text(text = (authHoldActionState.error ?: authHoldActionState.exception).toString())
}

@Composable
private fun AuthorizationHoldButtons(
    authRequestInProgress: Boolean,
    onCommit: () -> Unit,
    onRevert: () -> Unit,
) {
    Row {
        CommitButton(onCommit, authRequestInProgress)
        Spacer(modifier = Modifier.width(8.dp))
        RevertButton(onRevert, authRequestInProgress)
    }
}

@Composable
private fun CommitButton(onClick: () -> Unit, inProgress: Boolean) {
    Button(
        onClick = onClick,
        enabled = !inProgress,
    ) {
        if (!inProgress) Text("Commit") else CircularProgressIndicator()
    }
}

@Composable
private fun RevertButton(onClick: () -> Unit, inProgress: Boolean) {
    Button(
        onClick = onClick,
        enabled = !inProgress,
    ) {
        if (!inProgress) Text("Revert") else CircularProgressIndicator()
    }
}

@Composable
fun PaymentDetailsFailed(paymentDetailsRequest: RequestStatus<Payment>) {
    Column {
        Text("Loading payment details failed:")
        if (paymentDetailsRequest.error != null) {
            Text(paymentDetailsRequest.error.toString())
        }
        if (paymentDetailsRequest.exception != null) {
            Text(paymentDetailsRequest.error.toString())
        }
        if (paymentDetailsRequest.exception == null && paymentDetailsRequest.error == null) {
            Text("No details available")
        }
    }
}

@Composable
fun PaymentDetailsLoading() {
    CircularProgressIndicator()
}
