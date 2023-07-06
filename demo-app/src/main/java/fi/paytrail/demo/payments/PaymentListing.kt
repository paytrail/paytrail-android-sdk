package fi.paytrail.demo.payments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.paytrail.demo.R
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun PaymentListing(
    modifier: Modifier = Modifier,
    viewModel: PaymentListingViewModel,
    showPaymentDetails: (UUID) -> Unit,
) {
    val payments: List<PaymentData> = viewModel.payments.collectAsState(emptyList()).value
    if (payments.isEmpty()) {
        Text(
            modifier = Modifier.fillMaxSize(),
            text = stringResource(R.string.payment_list_no_payments_prompt),
        )
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        ) {
            payments.forEachIndexed { index, item ->
                item(item.paymentId) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (index == 0) Divider()
                        Column(
                            modifier = Modifier
                                .clickable { showPaymentDetails(item.paymentId) }
                                .fillMaxWidth(),
                        ) {
                            PaymentRequestItem(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .heightIn(min = 48.dp),
                                item,
                            )
                        }
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentRequestItem(modifier: Modifier = Modifier, item: PaymentData) {
    val state = item.state
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        if (state != null) {
            Text(text = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(state.timestamp))
            Text(text = "Local state: ${state.state}")
            Text(text = "Transaction ID: ${state.transactionId}")
        }
    }
}
