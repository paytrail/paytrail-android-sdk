package fi.paytrail.demo.tokenization

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.paytrail.demo.R
import fi.paytrail.demo.ui.theme.MyColors.PinkElement
import fi.paytrail.demo.ui.theme.MyColors.TextDisabledGrey
import fi.paytrail.sdk.apiclient.models.Card
import java.util.Locale

@Composable
fun CreditCard(modifier: Modifier = Modifier, card: Card) {
    val type = card.type ?: ""
    val number = "**** **** **** ${card.partialPan}"
    CreditCard(modifier, type, number)
}

@Composable
fun CreditCard(modifier: Modifier = Modifier, type: String, number: String) {
    CardSurface(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            Image(
                modifier = Modifier.width(32.dp),
                painter = painterResource(
                    id = when (type.lowercase(Locale.US)) {
                        "mastercard" -> R.drawable.mastercard
                        "visa" -> R.drawable.visa
                        else -> R.drawable.image_placeholder
                    },
                ),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )

            Spacer(modifier = Modifier.width(24.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = type, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = number,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDisabledGrey,
                )
            }
        }
    }
}

@Composable
@Preview
fun CreditCardLoading(modifier: Modifier = Modifier) {
    CardSurface(modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun CreditCardError(modifier: Modifier = Modifier) {
    CardSurface(modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Error loading card information",
            )
        }
    }
}

@Composable
private fun CardSurface(modifier: Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(width = 1.dp, color = PinkElement),
        content = content,
    )
}

@Composable
@Preview
private fun PreviewCreditCard() {
    CreditCard(type = "Mastercard", number = "**** **** **** 1234")
}
