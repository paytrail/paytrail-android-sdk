package fi.paytrail.demo.shoppingcart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.paytrail.demo.R
import fi.paytrail.demo.ui.theme.FilledButton
import fi.paytrail.demo.ui.theme.MyColors.Grey03
import fi.paytrail.demo.ui.theme.MyColors.Grey04
import fi.paytrail.demo.ui.theme.OutLineButton
import fi.paytrail.sdk.apiclient.models.Customer

/**
 * For this demo purpose, customer detail is static. In real scenario you would need to get real user data
 * that you will pass to [Customer] in order to create Payment request
 * Also the state would be coming outside this compose from ViewModel for example
 *
 */
@Composable
fun CustomerDetailScreen(
    modifier: Modifier = Modifier,
    cancelAction: () -> Unit,
    toPayAction: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        val focusManager = LocalFocusManager.current

        val firstName = remember {
            mutableStateOf("Maija")
        }
        val lastName = remember {
            mutableStateOf("Neikalainen")
        }
        val phoneNumbere = remember {
            mutableStateOf("044231122")
        }
        val email = remember {
            mutableStateOf("m.m@demo.fi")
        }
        val address = remember {
            mutableStateOf("Loremipsunkuja 1b")
        }
        val postalCode = remember {
            mutableStateOf("00100")
        }
        val postalAddress = remember {
            mutableStateOf("Helsinki")
        }
        val checkBoxState = remember {
            mutableStateOf(false)
        }
        val country = remember {
            mutableStateOf("Finland")
        }
        Text(
            text = "Customer Information",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(26.dp))

        FormInputField(
            placeholder = "First name",
            imeAction = ImeAction.Next,
            keyBoardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            text = firstName.value,
            onChange = {
                firstName.value = it
            },
        )
        Spacer(modifier = Modifier.height(26.dp))

        FormInputField(
            placeholder = "Last name",
            imeAction = ImeAction.Next,
            keyBoardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            text = lastName.value,
        )
        Spacer(modifier = Modifier.height(26.dp))

        FormInputField(
            placeholder = "Phone number",
            imeAction = ImeAction.Next,
            keyBoardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            text = phoneNumbere.value,
        )

        Spacer(modifier = Modifier.height(26.dp))

        FormInputField(
            placeholder = "Email",
            imeAction = ImeAction.Next,
            keyBoardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            text = email.value,
        )
        Spacer(modifier = Modifier.height(26.dp))

        FormInputField(
            placeholder = "Address",
            imeAction = ImeAction.Next,
            keyBoardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            text = address.value,
        )
        Spacer(modifier = Modifier.height(26.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            FormInputField(
                modifier = modifier
                    .weight(1f)
                    .padding(end = 14.dp),
                placeholder = "Zip code",
                imeAction = ImeAction.Next,
                keyBoardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                text = postalCode.value,
            )
            FormInputField(
                placeholder = "Postal address",
                modifier = modifier.weight(1f),
                imeAction = ImeAction.Next,
                keyBoardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                text = postalAddress.value,
            )
        }
        Spacer(modifier = Modifier.height(26.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = checkBoxState.value, onCheckedChange = {
                checkBoxState.value = it
            })
            Text(text = "I have read and accept the order and contract terms *")
        }
        Spacer(modifier = Modifier.height(26.dp))
        FormInputField(
            placeholder = "Country",
            imeAction = ImeAction.Next,
            keyBoardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            text = country.value,
        )
        Spacer(modifier = Modifier.height(26.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            OutLineButton(text = stringResource(id = R.string.payment_button_cancel)) {
                cancelAction.invoke()
            }
            FilledButton(
                text = stringResource(id = R.string.payment_button_payment),
                isEnabled = checkBoxState.value,
            ) {
                toPayAction.invoke()
            }
        }
    }
}

@Composable
fun FormInputField(
    modifier: Modifier = Modifier,
    text: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    onChange: (String) -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyBoardActions: KeyboardActions = KeyboardActions(),
    isEnabled: Boolean = true,
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = text,
        onValueChange = onChange,
        leadingIcon = leadingIcon,
        textStyle = TextStyle(fontSize = 18.sp),
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = keyBoardActions,
        enabled = isEnabled,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Grey03,
            unfocusedLabelColor = Grey04,
        ),
        label = {
            Text(text = placeholder)
        },
    )
}
