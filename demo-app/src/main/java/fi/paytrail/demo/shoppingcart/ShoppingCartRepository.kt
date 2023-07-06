package fi.paytrail.demo.shoppingcart

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

// TODO: Extract to model package
data class ShoppingCartRow(
    val id: UUID,
    val amount: Long,
    val price: BigDecimal,
    val vatPercentage: Long,
) {
    val totalPrice by lazy { amount * price }
}

// TODO: Extract to model package
data class ShoppingCart(
    val items: Map<UUID, ShoppingCartRow>,
) {
    val numberOfRows: Int by lazy { items.size }
    val totalAmount: BigDecimal by lazy {
        items.values.sumOf { it.totalPrice }
    }
}

val fakeCart = ShoppingCart(
    items = listOf(
        ShoppingCartRow(
            id = UUID.fromString("6391f2df-0dae-4fa3-ba1d-037273b27a4b"),
            amount = 1,
            price = BigDecimal.valueOf(30),
            vatPercentage = 24,
        ),
        ShoppingCartRow(
            id = UUID.fromString("2e3f6d5a-c33b-46c9-9942-98bf02651e23"),
            amount = 1,
            price = BigDecimal.valueOf(20),
            vatPercentage = 24,
        ),
        ShoppingCartRow(
            id = UUID.fromString("c739864b-0307-4cba-9101-60990c449da0"),
            amount = 2,
            price = BigDecimal.valueOf(40),
            vatPercentage = 24,
        ),
    ).associateBy { it.id },
)

@Singleton
class ShoppingCartRepository @Inject constructor() {

    private val sf = MutableStateFlow(fakeCart)

    fun cart(): Flow<ShoppingCart> {
        return sf
    }

    fun addItem(item: ShoppingCartRow) {
        sf.update { it.copy(items = it.items + (item.id to item)) }
    }

    fun removeItem(id: UUID) {
        sf.update { it.copy(items = it.items - id) }
    }

    fun clear() {
        sf.value = ShoppingCart(items = emptyMap())
    }

    /**
     * Convert the current shopping cart contents into a [PaymentRequest].
     * Each call creates [PaymentRequest] with new reference & stamps.
     */
    fun cartAsPaymentRequest(): PaymentRequest {
        val cart = sf.value

        return PaymentRequest(
            stamp = "PO-stamp-${UUID.randomUUID()}",
            reference = "PO-ref-${UUID.randomUUID()}",
            amount = (cart.totalAmount * 100).toLong(),
            currency = Currency.EUR,
            language = Language.EN,
            customer = Customer(email = "erkki.esimerkki@example.com"),
            items = cart.items.values.map {
                Item(
                    unitPrice = (it.price * 100).toLong(),
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
