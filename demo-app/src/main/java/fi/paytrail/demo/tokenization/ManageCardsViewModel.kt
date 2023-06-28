package fi.paytrail.demo.tokenization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.paytrail.demo.util.RequestStatus
import fi.paytrail.sdk.apiclient.models.TokenizationRequestResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TokenizedCreditCard(
    val tokenizationId: String,
    val response: TokenizationRequestResponse?,
)

@HiltViewModel
class ManageCardsViewModel @Inject constructor(val repo: SavedCardsRepository) : ViewModel() {

    private val tokenizationIds = repo.observeSavedTokenizationIDs()

    val cards: Flow<List<Pair<String, Flow<RequestStatus<TokenizedCreditCard>>>>> =
        tokenizationIds.map { ids ->
            ids.map { tokenizationId ->
                val requestFlow = getTokenizationResult(tokenizationId)
                tokenizationId to requestFlow.map { requestStatus ->
                    requestStatus.map { tokenizationResponse ->
                        TokenizedCreditCard(tokenizationId, tokenizationResponse)
                    }
                }
            }
        }.shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            replay = 1,
        )

    private fun getTokenizationResult(tokenizationId: String) = repo.getToken(tokenizationId)
        .shareIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            replay = 1,
        ) as Flow<RequestStatus<TokenizationRequestResponse>>

    fun removeCard(tokenizationId: String) = viewModelScope.launch { repo.removeTokenizationId(tokenizationId) }
}
