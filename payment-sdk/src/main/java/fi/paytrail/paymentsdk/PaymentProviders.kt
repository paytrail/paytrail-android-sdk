package fi.paytrail.paymentsdk

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import de.charlex.compose.HtmlText
import fi.paytrail.paymentsdk.model.PaymentMethod
import fi.paytrail.paymentsdk.model.PaymentMethodGroup
import fi.paytrail.paymentsdk.theme.PaytrailColors.Grey02
import fi.paytrail.paymentsdk.theme.Poppins
import fi.paytrail.sdk.apiclient.models.PaymentMethodProvider

val PaytrailTextChoosePaymentMethodHeading = TextStyle(
    fontFamily = Poppins,
    fontWeight = W700,
    fontSize = 24.sp,
    lineHeight = 36.sp,
)

val PaytrailTextPaymentGroupHeading = TextStyle(
    fontFamily = Poppins,
    fontWeight = W700,
    fontSize = 16.sp,
    lineHeight = 24.sp,
)

val PaytrailTextTermsAndConditions = TextStyle(
    fontFamily = Poppins,
    fontWeight = W300,
    fontSize = 12.sp,
    lineHeight = 17.sp,
    color = Grey02,
)

@Composable
fun PaymentProviders(
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel,
) {
    val providers = viewModel.paymentMethodGroups.observeAsState(emptyList()).value
    val terms = viewModel.paymentTerms.observeAsState("").value

    if (providers.isNotEmpty()) {
        PaymentProviderListing(
            modifier = modifier,
            terms = terms,
            groups = providers,
            onPaymentMethodSelected = viewModel::startPayment,
        )
    } else {
        NoPaymentProvidersAvailable(modifier)
    }
}

@Composable
private fun PaymentProviderListing(
    modifier: Modifier = Modifier,
    terms: String?,
    groups: List<PaymentMethodGroup>,
    onPaymentMethodSelected: (PaymentMethod) -> Unit = {},
) {
    Column(
        modifier = modifier
            .semantics { testTag = "PaymentProvidersListing" }
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 32.dp),
    ) {
        Text(
            text = "Choose payment method",
            style = PaytrailTextChoosePaymentMethodHeading,
        )
        if (terms != null) {
            PaytrailTerms(terms)
        }
        for (group in groups) {
            key(group) {
                PaymentProviderGroup(group, onPaymentMethodSelected)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentProviderGroup(
    group: PaymentMethodGroup,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
) {
    Column(modifier = Modifier.semantics { testTag = "PaymentProviderGroup" }) {
        PaymentProviderGroupHeader(group)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            for (method in group.paymentMethods) {
                PaymentProvider(
                    modifier = Modifier,
                    item = method,
                    onClick = {
                        onPaymentMethodSelected(method)
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
fun PreviewPaytrailTerms() {
    PaytrailTerms(
        terms = "By selecting a payment method, you agree to our <a href=\\\"https://www.paytrail.com/kuluttaja/maksupalveluehdot\\\" target=\\\"_blank\\\">payment service terms & conditions</a>",
    )
}

@Composable
fun PaytrailTerms(terms: String) {
    // TODO: Consider adding a composition-local LocalUriHandler to open
    //       the terms in a WebView instead of platform default browser.
    HtmlText(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
        text = terms,
        style = PaytrailTextTermsAndConditions,
    )
}

@Composable
fun PaymentProviderGroupHeader(group: PaymentMethodGroup) {
    Text(
        modifier = Modifier.semantics {
            testTag = "PaymentProviderGroupHeader"
        },
        text = group.name,
        style = PaytrailTextPaymentGroupHeading,
    )
}

@Composable
private fun PaymentProvider(
    modifier: Modifier = Modifier,
    item: PaymentMethod,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier.size(width = 100.dp, height = 64.dp)
            .background(color = Color.White)
            .semantics {
                testTag = "PaymentProvider"
                contentDescription = item.name
            },
        shape = RoundedCornerShape(6.dp),
        onClick = onClick,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.svg)
                    .placeholder(drawableResId = R.drawable.payment_provider_placeholder)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                error = painterResource(id = R.drawable.payment_provider_error),
                contentScale = ContentScale.Fit,
                contentDescription = item.name,
                alignment = Alignment.Center,
            )
        }
    }
}

@Composable
@Preview
private fun PreviewPaymentProvider() {
    PaymentProvider(
        modifier = Modifier.padding(4.dp),
        item = PaymentMethod(
            PaymentMethodProvider(
                id = "5923840b-2f97-4f6b-9683-e59660e862fd",
                name = "Payment Provider for Preview",
                svg = "https://upload.wikimedia.org/wikipedia/commons/5/5d/Duke_%28Java_mascot%29_waving.svg",
                icon = "https://en.wikipedia.org/wiki/File:Duke_%28Java_mascot%29_waving.svg#/media/File:Duke_Wave.png",
                group = PaymentMethodProvider.Group.Mobile,
                url = "https://ecom.example.com",
                parameters = emptyList(),
            ),
        ),
    )
}

fun Context.toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

@Composable
private fun NoPaymentProvidersAvailable(modifier: Modifier = Modifier) {
    Text(modifier = modifier, text = stringResource(R.string.no_payment_providers_available))
}
