package fi.paytrail.demo.tokenization

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.paytrail.demo.R
import fi.paytrail.demo.util.RequestStatus
import fi.paytrail.sdk.apiclient.models.Card
import kotlinx.coroutines.flow.Flow

@Composable
fun ManageCards(
    modifier: Modifier,
    viewModel: ManageCardsViewModel,
    addCardAction: () -> Unit,
) {
    val cards: List<Pair<String, Flow<RequestStatus<TokenizedCreditCard>>>> = viewModel.cards
        .collectAsState(initial = emptyList())
        .value

    Column(modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (cards.isNotEmpty()) {
                CreditCardListing(
                    cards = cards,
                    onCardLongClick = { tokenizationId, _ -> viewModel.removeCard(tokenizationId) },
                )
            } else {
                NoSavedCards()
            }
        }

        ManageCreditCardsBottomPanel(addCardAction = addCardAction)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CreditCardListing(
    cards: List<Pair<String, Flow<RequestStatus<TokenizedCreditCard>>>>,
    onCardClick: (String, Card?) -> Unit = { _, _ -> },
    onCardLongClick: (String, Card?) -> Unit = { _, _ -> },
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 32.dp,
            start = 16.dp,
            end = 16.dp,
        ),
    ) {
        cards.forEachIndexed { index, cardRequestFlow ->
            val (tokenizationId, requestStatusFlow) = cardRequestFlow
            item(tokenizationId) {
                val requestStatus =
                    requestStatusFlow.collectAsState(initial = RequestStatus.loading()).value
                Column {
                    val viewSizeModifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp)
                        .combinedClickable(
                            onLongClick = {
                                onCardLongClick(
                                    tokenizationId,
                                    requestStatus.value?.response?.card,
                                )
                            },
                            onClick = {
                                onCardClick(
                                    tokenizationId,
                                    requestStatus.value?.response?.card,
                                )
                            },
                        )

                    when {
                        requestStatus.isLoading -> {
                            CreditCardLoading(modifier = viewSizeModifier)
                        }

                        requestStatus.isSuccess -> {
                            CreditCard(
                                modifier = viewSizeModifier,
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
