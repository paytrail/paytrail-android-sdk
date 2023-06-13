package fi.paytrail.demo

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.paytrail.demo.repository.ShoppingCartRow
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
) {
    val items = viewModel.items.collectAsState(initial = emptyList()).value
    val total = viewModel.totalAmount.collectAsState(initial = BigDecimal.ZERO).value
    val rowCount = viewModel.rowCount.collectAsState(initial = 0).value

    ShoppingCart(modifier, items, rowCount, total, payAction)
}

@Composable
private fun ShoppingCart(
    modifier: Modifier,
    items: List<ShoppingCartRow>,
    rowCount: Int,
    total: BigDecimal,
    payAction: () -> Unit = {}
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
        )

        ShoppingCartBottomBar(total, items, payAction)
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
            text = stringResource(R.string.shopping_cart_screen_title, rowCount),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Divider()
    }
}

@Composable
private fun ShoppingCartListing(modifier: Modifier, items: List<ShoppingCartRow>) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
            ShoppingCartItem(item)
            if (index < items.lastIndex) {
                Divider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun ShoppingCartBottomBar(
    total: BigDecimal,
    items: List<ShoppingCartRow>,
    payAction: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Divider()
        Spacer(modifier = Modifier.height(4.dp))
        Text("Total: ${currencyFormatter.format(total)} €")
        PayButton(enabled = items.isNotEmpty(), onClick = payAction)
    }
}

@Composable
private fun PayButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val context = LocalContext.current

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
private fun ShoppingCartItem(item: ShoppingCartRow) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.image_placeholder),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.weight(1.0f),
                text = item.id.toString(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = "${item.amount} kpl",
                )
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = "${currencyFormatter.format(item.totalPrice)} €",
                )
            }
        }
    }
}

// TODO: More previews

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
fun PreviewShoppingCartItem() {
    ShoppingCartItem(
        ShoppingCartRow(
            id = UUID.fromString("6427e0c2-382d-4f03-99e5-413fff4d0afb"),
            amount = 2,
            price = BigDecimal("2.99"),
        ),
    )
}
