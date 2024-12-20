package fi.paytrail.demo.shoppingcart

import fi.paytrail.demo.R
import fi.paytrail.demo.util.times
import fi.paytrail.sdk.apiclient.models.Callbacks
import fi.paytrail.sdk.apiclient.models.Currency
import fi.paytrail.sdk.apiclient.models.Customer
import fi.paytrail.sdk.apiclient.models.Item
import fi.paytrail.sdk.apiclient.models.Language
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Replace ShoppingCartRepository by handling state completely
//       in the ShoppingCartViewModel?

val fakeCart = ShoppingCart(
    items = listOf(
        ShoppingCartRow(
            name = "Paytrail Umbrella",
            id = UUID.fromString("2e3f6d5a-c33b-46c9-9942-98bf02651e23"),
            amount = 1,
            unitPrice = BigDecimal.valueOf(15),
            fakeImage = R.drawable.product_img2,
            vatPercentage = BigDecimal(25.5),
        ),
        ShoppingCartRow(
            name = "Paytrail Drinking Bottle",
            id = UUID.fromString("c739864b-0307-4cba-9101-60990c449da0"),
            amount = 2,
            unitPrice = BigDecimal.valueOf(20),
            fakeImage = R.drawable.product_img1,
            vatPercentage = BigDecimal(14),
        ),
    ).associateBy { it.id },
)

@Singleton
class ShoppingCartRepository @Inject constructor() {

    private val shoppingCartState = MutableStateFlow(fakeCart)

    fun cart(): Flow<ShoppingCart> {
        return shoppingCartState
    }

    fun incrementItemAmount(id: UUID) {
        shoppingCartState.value.items[id]?.let {
            shoppingCartState.value = ShoppingCart(
                items = shoppingCartState.value.items + (id to it.copy(amount = it.amount + 1)),
            )
        }
    }
    fun decrementItemAmount(id: UUID) {
        shoppingCartState.value.items[id]?.let {
            if (it.amount > 1) {
                shoppingCartState.value = ShoppingCart(
                    items = shoppingCartState.value.items + (id to it.copy(amount = it.amount - 1)),
                )
            }
        }
    }

    fun addItem(item: ShoppingCartRow) {
        shoppingCartState.update { it.copy(items = it.items + (item.id to item)) }
    }

    fun removeItem(id: UUID) {
        shoppingCartState.update { it.copy(items = it.items - id) }
    }

    fun clear() {
        shoppingCartState.value = ShoppingCart(items = emptyMap())
    }

    /**
     * Convert the current shopping cart contents into a [PaymentRequest].
     * Each call creates [PaymentRequest] with new reference & stamps.
     * For this demo purpose we are creating an empty customer
     */
    fun cartAsPaymentRequest(): PaymentRequest {
        val cart = shoppingCartState.value

        return PaymentRequest(
            stamp = "PO-stamp-${UUID.randomUUID()}",
            reference = "PO-ref-${UUID.randomUUID()}",
            amount = (cart.totalAmount * 100).toLong(),
            currency = Currency.EUR,
            language = Language.EN,
            customer = Customer(email = ""),
            items = cart.items.values.map {
                Item(
                    unitPrice = (it.unitPrice * 100).toLong(),
                    units = it.amount,
                    vatPercentage = it.vatPercentage,
                    productCode = it.id.toString(),
                    stamp = "Item-stamp-${UUID.randomUUID()}",
                    reference = "Item-ref-${UUID.randomUUID()}",
                )
            },
            redirectUrls = Callbacks(
                success = "https://ecom.example.org/success",
                cancel = "https://ecom.example.org/cancel",
            ),
        )
    }
}
