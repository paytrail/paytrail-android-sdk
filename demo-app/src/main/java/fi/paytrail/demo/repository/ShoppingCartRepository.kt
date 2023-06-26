package fi.paytrail.demo.repository

import fi.paytrail.demo.util.times
import fi.paytrail.sdk.apiclient.models.Callbacks
import fi.paytrail.sdk.apiclient.models.Customer
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

// TODO: Replace ShoppingCartRepository by handling state completely
//       in the ShoppingCartViewModel?

// TODO: Extract to model package
data class ShoppingCartRow(
    val id: UUID,
    val amount: Int,
    val price: BigDecimal,
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
        ),
        ShoppingCartRow(
            id = UUID.fromString("2e3f6d5a-c33b-46c9-9942-98bf02651e23"),
            amount = 1,
            price = BigDecimal.valueOf(20),
        ),
        ShoppingCartRow(
            id = UUID.fromString("c739864b-0307-4cba-9101-60990c449da0"),
            amount = 2,
            price = BigDecimal.valueOf(40),
        ),
    ).associateBy { it.id },
)

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
            amount = cart.totalAmount.multiply(100.toBigDecimal()).toLong(),
            currency = PaymentRequest.Currency.EUR,
            language = PaymentRequest.Language.EN,
            customer = Customer(email = "erkki.esimerkki@example.com"),
            redirectUrls = Callbacks(
                success = "https://ecom.example.org/success",
                cancel = "https://ecom.example.org/cancel",
            ),
        )
    }
}
