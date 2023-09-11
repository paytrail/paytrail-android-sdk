package fi.paytrail.demo.tokenization

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.paytrail.demo.R
import fi.paytrail.paymentsdk.RequestStatus
import fi.paytrail.paymentsdk.tokenization.TokenPaymentChargeType
import fi.paytrail.paymentsdk.tokenization.TokenPaymentType
import fi.paytrail.sdk.apiclient.models.TokenizationRequestResponse
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenizedCreditCardsScreen(
    modifier: Modifier = Modifier,
    viewModel: TokenizedCreditCardsViewModel,
    payWithCardAction: (String, TokenPaymentType, TokenPaymentChargeType) -> Unit,
    addCardAction: () -> Unit,
) {
    val cards: List<Pair<String, Flow<RequestStatus<TokenizedCreditCard>>>> = viewModel.cards
        .collectAsState(initial = emptyList())
        .value

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val sheetTokenizationId =
        viewModel.actionsMenuTokenizationId.collectAsState(initial = null).value
    LaunchedEffect(sheetTokenizationId) {
        if (sheetTokenizationId.isNullOrEmpty()) bottomSheetState.hide() else bottomSheetState.show()
    }

    Column(modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (cards.isNotEmpty()) {
                TokenizedCreditCardListing(
                    cards = cards,
                    onCardClick = { tokenizationId, _ ->
                        viewModel.showCardActions(
                            tokenizationId,
                        )
                    },
                )
            } else {
                NoSavedCards()
            }
        }

        // TODO: Show most recent auth hold & allow commit/revoke

        ManageCreditCardsBottomPanel(addCardAction = addCardAction)

        if (bottomSheetState.currentValue != SheetValue.Hidden) {
            CardActionsBottomSheet(
                viewModel = viewModel,
                payWithCardAction = payWithCardAction,
                bottomSheetState = bottomSheetState,
                sheetTokenizationId = sheetTokenizationId,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CardActionsBottomSheet(
    viewModel: TokenizedCreditCardsViewModel,
    payWithCardAction: (String, TokenPaymentType, TokenPaymentChargeType) -> Unit,
    bottomSheetState: SheetState,
    sheetTokenizationId: String?,
) {
    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = viewModel::hideCardActions,
        sheetState = bottomSheetState,
        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
        scrimColor = Color.Black.copy(alpha = 0.4f),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Divider()
            BottomSheetAction(
                text = stringResource(R.string.card_action_pay_cit_charge),
                onClick = {
                    payWithCardAction(
                        sheetTokenizationId!!,
                        TokenPaymentType.CIT,
                        TokenPaymentChargeType.CHARGE,
                    )
                },
            )
            Divider()
            BottomSheetAction(
                text = stringResource(R.string.card_action_pay_cit_auth_hold),
                onClick = {
                    payWithCardAction(
                        sheetTokenizationId!!,
                        TokenPaymentType.CIT,
                        TokenPaymentChargeType.AUTH_HOLD,
                    )
                },
            )
            Divider()
            BottomSheetAction(
                text = stringResource(R.string.card_action_pay_mit_charge),
                onClick = {
                    payWithCardAction(
                        sheetTokenizationId!!,
                        TokenPaymentType.MIT,
                        TokenPaymentChargeType.CHARGE,
                    )
                },
            )
            Divider()
            BottomSheetAction(
                text = stringResource(R.string.card_action_pay_mit_auth_hold),
                onClick = {
                    payWithCardAction(
                        sheetTokenizationId!!,
                        TokenPaymentType.MIT,
                        TokenPaymentChargeType.AUTH_HOLD,
                    )
                },
            )
            Divider()
            BottomSheetAction(
                text = stringResource(R.string.card_action_remove_card),
                onClick = { viewModel.removeCard(sheetTokenizationId!!) },
            )
            Divider()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview
@Composable
fun BottomSheetAction(
    modifier: Modifier = Modifier,
    text: String = "Action",
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
fun TokenizedCreditCardListing(
    modifier: Modifier = Modifier,
    cards: List<Pair<String, Flow<RequestStatus<TokenizedCreditCard>>>>,
    onCardClick: (String, TokenizationRequestResponse?) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 16.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp,
            )
            .height(intrinsicSize = IntrinsicSize.Min),
    ) {
        cards.forEachIndexed { index, cardRequestFlow ->
            val (tokenizationId, requestStatusFlow) = cardRequestFlow
            key(tokenizationId) {
                val requestStatus =
                    requestStatusFlow.collectAsState(initial = RequestStatus.loading()).value
                Column {
                    val viewSizeModifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp)
                        .height(intrinsicSize = IntrinsicSize.Min)

                    when {
                        requestStatus.isLoading -> {
                            CreditCardLoading(modifier = viewSizeModifier)
                        }

                        requestStatus.isSuccess -> {
                            CreditCard(
                                modifier = viewSizeModifier.clickable(
                                    enabled = requestStatus.value?.response != null,
                                    onClick = {
                                        onCardClick(
                                            tokenizationId,
                                            requestStatus.value?.response,
                                        )
                                    },
                                ),
                                card = requireNotNull(requestStatus.value?.response?.card),
                            )
                        }

                        requestStatus.isError -> {
                            // Real application probably wants to handle the error already
                            // in repository. Depending on the error, you might want to
                            // either hide the card from user, or remove the tokenizationId
                            // from your database, or mark it as unusable..
                            CreditCardError(modifier = viewSizeModifier)
                        }
                    }
                    if (index < cards.lastIndex) Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
@Preview
private fun NoSavedCards() {
    Text(
        modifier = Modifier.fillMaxSize(),
        textAlign = TextAlign.Center,
        text = "No saved cards; add one!",
    )
}

@Composable
@Preview
private fun ManageCreditCardsBottomPanel(addCardAction: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Divider()
        Spacer(modifier = Modifier.height(4.dp))
        val context = LocalContext.current
        Button(
            modifier = Modifier
                .widthIn(min = 80.dp)
                .padding(top = 8.dp, bottom = 16.dp),
            onClick = addCardAction,
        ) {
            Text(stringResource(R.string.manage_cards_button_add_card))
        }
    }
}
