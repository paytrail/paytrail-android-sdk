package fi.paytrail.demo.shoppingcart

import fi.paytrail.demo.util.times
import java.math.BigDecimal
import java.util.UUID

data class ShoppingCartRow(
    val name: String,
    val id: UUID,
    val amount: Long,
    val unitPrice: BigDecimal,
    val vatPercentage: Long,
    val fakeImage: Int, // You can replace it with a string that contain url of the image
) {
    val totalPrice by lazy { amount * unitPrice }
}
