package fi.paytrail.demo.shoppingcart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.paytrail.demo.R
import fi.paytrail.demo.ui.theme.MyColors.Grey02
import java.math.BigDecimal
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.UUID

val currencyFormatter = java.text.DecimalFormat(
    "0.00",
    DecimalFormatSymbols(Locale("fi", "FI")),
)

// TODO: Allow adding items to cart
// TODO: Allow removing items from cart
// TODO: Styling

@Composable
fun ShoppingCartScreen(
    modifier: Modifier = Modifier,
    viewModel: ShoppingCartViewModel,
    payAction: () -> Unit,
    payAndAddCardAction: () -> Unit,
    cardsAction: () -> Unit,
    showPaymentHistory: () -> Unit,
) {
    val items = viewModel.items.collectAsState(initial = emptyList()).value
    val total = viewModel.totalAmount.collectAsState(initial = BigDecimal.ZERO).value

    ShoppingCartScreen(
        modifier = modifier,
        items = items,
        total = total,
        payAction = payAction,
        payAndAddCardAction = payAndAddCardAction,
        cardsAction = cardsAction,
        showPaymentHistory = showPaymentHistory,
        onIncrement = {
            viewModel.incrementAmount(it)
        },
        onDecrement = {
            viewModel.decrementAmount(it)
        }
    )
}

@Composable
private fun ShoppingCartScreen(
    modifier: Modifier,
    items: List<ShoppingCartRow>,
    total: BigDecimal,
    payAction: () -> Unit = {},
    payAndAddCardAction: () -> Unit = {},
    cardsAction: () -> Unit = {},
    showPaymentHistory: () -> Unit = {},
    onIncrement: (UUID) -> Unit = {},
    onDecrement: (UUID) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        ShoppingCartListing(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            items = items,
            total = total,
            onIncrement,
            onDecrement
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
private fun ShoppingCartListing(
    modifier: Modifier,
    items: List<ShoppingCartRow>,
    total: BigDecimal,
    onIncrement: (UUID) -> Unit,
    onDecrement: (UUID) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            top = 24.dp,
            bottom = 32.dp,
            start = 24.dp,
            end = 24.dp,
        ),
    ) {
        item {
            Text(
                text = stringResource(id = R.string.shopping_cart_title),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        items(items, key = { item -> item.id }) { item ->
            ShoppingCartItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                item = item,
                onIncrement,
                onDecrement
            )
        }

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider(
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
                    color = Grey02,
                )
                ShoppingCartTotalPrice(
                    modifier = Modifier.fillMaxWidth(),
                    totalPrice = total,
                )
            }
        }
    }
}

@Composable
private fun ShoppingCartTotalPrice(modifier: Modifier, totalPrice: BigDecimal) {
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontSize = 20.sp,
            fontWeight = W700,
        ),
    ) {
        Row(modifier = modifier) {
            Text(stringResource(R.string.shopping_cart_total))
            Spacer(modifier = Modifier.weight(1f))
            Text("${currencyFormatter.format(totalPrice)} €")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ShoppingCartBottomBar(
    items: List<ShoppingCartRow>,
    payAction: () -> Unit,
    cardsAction: () -> Unit,
    payAndAddCardAction: () -> Unit,
    showPaymentHistory: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Divider()
        Spacer(modifier = Modifier.height(4.dp))

        FlowRow {
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
    onIncrement: (UUID) -> Unit = {},
    onDecrement: (UUID) -> Unit = {}
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
                modifier = Modifier.size(64.dp),
                painter = painterResource(id = item.fakeImage),
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
                    fontWeight = W700,
                    text = "${currencyFormatter.format(item.unitPrice)} €"
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
                    Box(
                        modifier = Modifier
                            .sizeIn(minWidth = 38.dp, minHeight = 38.dp)
                            .clickable { onDecrement(item.id) },

                        contentAlignment = Alignment.Center,
                    ) {
                        // TODO: replace text with icon
                        Text(
                            modifier = Modifier,
                            text = "-",
                        )
                    }

                    VerticalDivider()

                    Text(
                        modifier = Modifier.weight(1f),
                        text = "${item.amount}",
                        textAlign = TextAlign.Center,
                    )
                    VerticalDivider()

                    Box(
                        modifier = Modifier
                            .sizeIn(minWidth = 38.dp, minHeight = 38.dp)
                            .clickable { onIncrement(item.id) },
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
private fun VerticalDivider(
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
            unitPrice = BigDecimal("2.99"),
            vatPercentage = 24,
            fakeImage = R.drawable.image_placeholder
        ),
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xff0000, widthDp = 330, heightDp = 128)
fun PreviewShoppingCartItem_LongName() {
    ShoppingCartItem(
        modifier = Modifier.padding(16.dp),
        item = ShoppingCartRow(
            name = "Preview Item with a reasonably long name",
            id = UUID.fromString("6427e0c2-382d-4f03-99e5-413fff4d0afb"),
            amount = 2,
            unitPrice = BigDecimal("2345.67"),
            vatPercentage = 24,
            fakeImage = R.drawable.image_placeholder
        ),
    )
}
