package fi.paytrail.demo.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.paytrail.demo.ui.theme.MyColors.Grey02
import fi.paytrail.demo.ui.theme.MyColors.PinkElement

@Composable
fun OutLineButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier
            .widthIn(min = 80.dp)
            .padding(top = 8.dp, end = 16.dp, start = 16.dp, bottom = 8.dp),
        onClick = { onClick.invoke() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Grey02,
        ),
        border = BorderStroke(1.dp, PinkElement),
    ) {
        Text(text)
    }
}

@Composable
fun FilledButton(modifier: Modifier = Modifier, text: String, isEnabled: Boolean = true, onClick: () -> Unit) {
    Button(
        modifier = modifier
            .widthIn(min = 80.dp)
            .padding(top = 8.dp, end = 16.dp, start = 16.dp, bottom = 8.dp),
        onClick = { onClick.invoke() },
        colors = ButtonDefaults.buttonColors(
            containerColor = PinkElement,
            contentColor = Color.White,
        ),
        enabled = isEnabled,
    ) {
        Text(text)
    }
}

@Preview
@Composable
fun PreviewButtons() {
    Column {
        OutLineButton("Cancel") {}
        Spacer(modifier = Modifier.height(16.dp))
        FilledButton(text = "To Payment") {}
        Spacer(modifier = Modifier.height(16.dp))
        FilledButton(text = "To Payment", isEnabled = false) {}
    }
}
