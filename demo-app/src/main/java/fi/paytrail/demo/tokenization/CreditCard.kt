package fi.paytrail.demo.tokenization

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.paytrail.demo.R
import fi.paytrail.sdk.apiclient.models.Card

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
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // TODO: Add icons for MC/Visa/Discovery/AmEx/unknown...
            Image(
                painter = painterResource(id = R.drawable.image_placeholder),
                contentDescription = null,
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(text = type)
                Text(text = number)
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
        border = BorderStroke(width = 1.dp, color = Color.Black),
        content = content,
    )
}

@Composable
@Preview
private fun PreviewCreditCard() {
    CreditCard(type = "Mastercard", number = "**** **** **** 1234")
}
