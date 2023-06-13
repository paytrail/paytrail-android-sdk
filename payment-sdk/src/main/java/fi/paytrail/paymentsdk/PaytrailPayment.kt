package fi.paytrail.paymentsdk

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import java.util.concurrent.atomic.AtomicInteger

/*
enum class Currency(s: String) { EUR("EUR") }

enum class Language(s: String) { FI("FI"), SV("SV"), EN("EN") }

data class Merchant(
    val merchantId: String,
    val secret: String,
)

@Parcelize
data class Item(
    val unitPrice: BigDecimal,
    val units: Int,
    val vatPercentage: Int,
    val productCode: String,
    // TODO: Rest of the fields
) : Parcelable

@Parcelize
data class Customer(
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val vatId: String? = null,
) : Parcelable

@Parcelize
data class Callbacks(
    val success: String,
    val cancel: String,
) : Parcelable

/**
 * Known supported values for payment method groups.
 */
object PaymentMethodGroup {
    const val MOBILE = "mobile"
    const val BANK = "bank"
    const val CREDIT_CARD = "creditcard"
    const val CREDIT = "credit"
}

@Parcelize
data class Address(
    val streetAddress: String? = null,
    val postalCode: String? = null,
    val city: String? = null,
    val county: String? = null,
    val country: String? = null,
) : Parcelable

// XXX: Should this model already include
@Parcelize
data class PaymentOrder(
    val stamp: String,
    val reference: String,
    val amount: BigDecimal,
    val currency: Currency,
    val language: Language,
    val customer: Customer,
    val redirectUrls: Callbacks,
    val deliveryAddress: Address? = null,
    val invoicingAddress: Address? = null,
    val manualInvoiceActivation: Boolean? = null,
    val items: List<Item>? = null,
    val groups: List<String>? = null,
    val usePricesWithoutVat: Boolean? = null,
) : Parcelable
*/

private val paymentCompositionCounter = AtomicInteger(0)

// TODO: Provide payment state as a hoistable state object?
@Composable
fun PaytrailPayment(
    modifier: Modifier,
    payment: PaymentRequest,
    merchant: MerchantAccount = MerchantAccount.default,
) {
    val paymentCompositionId = remember(
        keys = arrayOf(
            payment,
            merchant,
        ),
    ) { "payment-${paymentCompositionCounter.incrementAndGet()}" }

    val localView = LocalView.current
    val viewModel = remember(paymentCompositionId) {
        val viewModelStoreOwner = localView.findViewTreeViewModelStoreOwner() ?: run {
            throw RuntimeException("No ViewModelStoreOwner associated with local view $localView")
        }

        ViewModelProvider(
            viewModelStoreOwner,
            PaymentViewModelFactory(payment, merchant),
        )[paymentCompositionId, PaymentViewModel::class.java]
    }

    PaytrailPayment(modifier, viewModel)
}

@Composable
internal fun PaytrailPayment(
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel,
) {
    val paymentStatus =
        viewModel.paymentStatus.observeAsState(initial = PaymentStatus.LOADING_PAYMENT_PROVIDERS).value

    // TODO: Set up & apply custom theming to relevant components

    Surface(modifier) {
        when (paymentStatus) {
            PaymentStatus.LOADING_PAYMENT_PROVIDERS -> LoadingPaymentMethods()
            PaymentStatus.SHOW_PAYMENT_PROVIDERS -> PaymentProviders(viewModel = viewModel)
            PaymentStatus.PAYMENT_IN_PROGRESS -> TODO()
            PaymentStatus.PAYMENT_ERROR -> TODO()
            PaymentStatus.PAYMENT_CANCELED -> TODO()
            PaymentStatus.PAYMENT_DONE -> TODO()
        }
    }
}

