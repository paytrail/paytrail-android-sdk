package fi.paytrail.demo.tokenization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.paytrail.paymentsdk.RequestStatus
import fi.paytrail.sdk.apiclient.models.TokenizationRequestResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * Represents a tokenized credit card.
 *
 * This data class holds the necessary details of a credit card that has been tokenized,
 * including the tokenization ID and the response that provides additional information
 * about the tokenization process.
 *
 * @param tokenizationId The unique identifier of the tokenization process.
 * @param response The response that provides additional details about the tokenization.
 */
data class TokenizedCreditCard(
    val tokenizationId: String,
    val response: TokenizationRequestResponse?,
)

@HiltViewModel
class TokenizedCreditCardsViewModel @Inject constructor(
    private val cardsRepository: TokenizedCardsRepository,
) : ViewModel() {

    private val tokenizationIds = cardsRepository.observeSavedTokenizationIDs()
    val actionsMenuTokenizationId = MutableStateFlow<String?>(null)

    // TODO: Cache card information (in repository) so adding/removing cards does not
    //       trigger reloads.
    val cards: Flow<List<Pair<String, Flow<RequestStatus<TokenizedCreditCard>>>>> =
        tokenizationIds.map { ids ->
            ids.map { tokenizationId ->
                val requestFlow = getTokenizedCardDetails(tokenizationId)
                tokenizationId to requestFlow.map { requestStatus ->
                    requestStatus.map { tokenizationResponse ->
                        TokenizedCreditCard(tokenizationId, tokenizationResponse)
                    }
                }
            }
        }.shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            replay = 1,
        )

    private fun getTokenizedCardDetails(tokenizationId: String) =
        cardsRepository.getToken(tokenizationId)
            .shareIn(
                viewModelScope,
                started = SharingStarted.Lazily,
                replay = 1,
            ) as Flow<RequestStatus<TokenizationRequestResponse>>

    fun removeCard(tokenizationId: String) {
        hideCardActions()
        viewModelScope.launch { cardsRepository.removeTokenizationId(tokenizationId) }
    }

    fun showCardActions(tokenizationId: String) {
        actionsMenuTokenizationId.value = tokenizationId
    }

    fun hideCardActions() {
        actionsMenuTokenizationId.value = null
    }
}
