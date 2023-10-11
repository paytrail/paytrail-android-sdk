package fi.paytrail.demo.shoppingcart

import java.math.BigDecimal
import java.util.UUID

data class ShoppingCart(
    val items: Map<UUID, ShoppingCartRow>,
) {
    val numberOfRows: Int by lazy { items.size }
    val totalAmount: BigDecimal by lazy {
        items.values.sumOf { it.totalPrice }
    }
}
