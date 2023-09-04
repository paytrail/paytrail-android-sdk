package fi.paytrail.demo.shoppingcart

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fi.paytrail.demo.R
import java.math.BigDecimal
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.UUID

private val currencyFormatter = java.text.DecimalFormat(
    "0.00",
    DecimalFormatSymbols(Locale("fi", "FI")),
)

// TODO: Allow adding items to cart
// TODO: Allow removing items from cart
// TODO: Styling

@Composable
fun ShoppingCart(
    modifier: Modifier = Modifier,
    viewModel: ShoppingCartViewModel,
    payAction: () -> Unit,
    payAndAddCardAction: () -> Unit,
    cardsAction: () -> Unit,
    showPaymentHistory: () -> Unit,
) {
    val items = viewModel.items.collectAsState(initial = emptyList()).value
    val total = viewModel.totalAmount.collectAsState(initial = BigDecimal.ZERO).value
    val rowCount = viewModel.rowCount.collectAsState(initial = 0).value

    ShoppingCart(
        modifier = modifier,
        items = items,
        rowCount = rowCount,
        total = total,
        payAction = payAction,
        payAndAddCardAction = payAndAddCardAction,
        cardsAction = cardsAction,
        showPaymentHistory = showPaymentHistory,
    )
}

@Composable
private fun ShoppingCart(
    modifier: Modifier,
    items: List<ShoppingCartRow>,
    rowCount: Int,
    total: BigDecimal,
    payAction: () -> Unit = {},
    payAndAddCardAction: () -> Unit = {},
    cardsAction: () -> Unit = {},
    showPaymentHistory: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        ShoppingCartTopBar(rowCount)

        ShoppingCartListing(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            items = items,
            total = total,
        )

        ShoppingCartBottomBar(
            items = items,
            payAction = payAction,
            payAndAddCardAction = payAndAddCardAction,
            cardsAction = cardsAction,
            showPaymentHistory = showPaymentHistory,
        )
    }
}

@Composable
private fun ShoppingCartTopBar(rowCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .height(intrinsicSize = IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.shopping_cart_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Divider()
    }
}

@Composable
private fun ShoppingCartListing(
    modifier: Modifier,
    items: List<ShoppingCartRow>,
    total: BigDecimal,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            top = 24.dp,
            bottom = 32.dp,
            start = 16.dp,
            end = 16.dp,
        ),
    ) {
        item {
            Text(stringResource(id = R.string.shopping_cart_title))
        }

        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
            ShoppingCartItem(
                modifier = Modifier.padding(vertical = 8.dp),
                item = item,
            )
        }

        item {
            Row {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.shopping_cart_total))
                Spacer(modifier = Modifier.weight(1f))
                Text("${currencyFormatter.format(total)} €")
            }
        }
    }
}

@Composable
private fun ShoppingCartBottomBar(
    items: List<ShoppingCartRow>,
    payAction: () -> Unit,
    cardsAction: () -> Unit,
    payAndAddCardAction: () -> Unit,
    showPaymentHistory: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Divider()
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            PayButton(enabled = items.isNotEmpty(), onClick = payAction)
            Spacer(modifier = Modifier.width(8.dp))
            PayAndAddCardButton(enabled = items.isNotEmpty(), onClick = payAndAddCardAction)
            Spacer(modifier = Modifier.width(8.dp))
            CardsButton(onClick = cardsAction)
            Spacer(modifier = Modifier.width(8.dp))
            HistoryButton(onClick = showPaymentHistory)
        }
    }
}

@Composable
fun CardsButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .widthIn(min = 80.dp)
            .padding(top = 8.dp, bottom = 16.dp),
        onClick = onClick,
    ) {
        Text(stringResource(R.string.shopping_cart_button_cards))
    }
}

@Composable
fun HistoryButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .widthIn(min = 80.dp)
            .padding(top = 8.dp, bottom = 16.dp),
        onClick = onClick,
    ) {
        Text(stringResource(R.string.shopping_cart_button_history))
    }
}

@Composable
private fun PayButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier
            .widthIn(min = 80.dp)
            .padding(top = 8.dp, bottom = 16.dp),
        onClick = onClick,
        enabled = enabled,
    ) {
        Text(stringResource(R.string.shopping_cart_button_pay))
    }
}

@Composable
private fun PayAndAddCardButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier
            .widthIn(min = 80.dp)
            .padding(top = 8.dp, bottom = 16.dp),
        onClick = onClick,
        enabled = enabled,
    ) {
        Text(stringResource(R.string.shopping_cart_button_pay_and_add))
    }
}

@Composable
private fun ShoppingCartItem(
    modifier: Modifier = Modifier,
    item: ShoppingCartRow,
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(7.dp),
            )
            .background(color = Color.White)
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.image_placeholder),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1.0f)) {
                Text(
                    modifier = Modifier,
                    text = item.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    modifier = Modifier,
                    text = "${currencyFormatter.format(item.totalPrice)} €",
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .widthIn(min = 138.dp)
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min)
                        .border(
                            border = BorderStroke(1.dp, Color.Gray),
                            shape = RoundedCornerShape(3.dp),
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // TODO: Adjust amounts on click
                    Box(
                        modifier = Modifier
                            .sizeIn(minWidth = 38.dp, minHeight = 38.dp)
                            .clickable { Log.i("ShoppingCart", "TODO: Decrement amount") },
                        contentAlignment = Alignment.Center,
                    ) {
                        // TODO: replace text with icon
                        Text(
                            modifier = Modifier,
                            text = "-",
                        )
                    }

                    HorizontalDivider()

                    Text(
                        modifier = Modifier.weight(1f),
                        text = "${item.amount}",
                        textAlign = TextAlign.Center,
                    )
                    HorizontalDivider()

                    // TODO: Adjust amounts on click
                    Box(
                        modifier = Modifier
                            .sizeIn(minWidth = 38.dp, minHeight = 38.dp)
                            .clickable { Log.i("ShoppingCart", "TODO: Decrement amount") },
                        contentAlignment = Alignment.Center,
                    ) {
                        // TODO: replace text with icon
                        Text(
                            modifier = Modifier,
                            text = "+",
                        )
                    }

                }
            }
        }
    }
}

@Composable
private fun HorizontalDivider(
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
) {
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp),
        thickness = thickness,
        color = color,
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xff0000, widthDp = 330, heightDp = 128)
fun PreviewShoppingCartItem() {
    ShoppingCartItem(
        modifier = Modifier.padding(16.dp),
        item = ShoppingCartRow(
            name = "Preview Item",
            id = UUID.fromString("6427e0c2-382d-4f03-99e5-413fff4d0afb"),
            amount = 2,
            price = BigDecimal("2.99"),
            vatPercentage = 24,
        ),
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xff0000, widthDp = 330, heightDp = 128)
fun PreviewShoppingCartItemLongName() {
    ShoppingCartItem(
        modifier = Modifier.padding(16.dp),
        item = ShoppingCartRow(
            name = "Preview Item with a reasonably long name",
            id = UUID.fromString("6427e0c2-382d-4f03-99e5-413fff4d0afb"),
            amount = 2,
            price = BigDecimal("2345.67"),
            vatPercentage = 24,
        ),
    )
}
