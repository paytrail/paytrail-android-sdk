package fi.paytrail.demo.tokenization

import fi.paytrail.paymentsdk.RequestStatus
import fi.paytrail.paymentsdk.flowApiRequest
import fi.paytrail.sdk.apiclient.apis.TokenPaymentsApi
import fi.paytrail.sdk.apiclient.models.TokenizationRequestResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository to manage saved tokenization IDs
 */
@Singleton
class TokenizedCardsRepository @Inject constructor(
    private val tokenPaymentsApi: TokenPaymentsApi,
    private val cardDao: TokenizedCardDao,
) {

    suspend fun saveTokenizationId(tokenizationId: String) {
        cardDao.insertAll(TokenizedCard(tokenizationId))
    }

    suspend fun removeTokenizationId(tokenizationId: String) {
        cardDao.delete(TokenizedCard(tokenizationId))
    }

    fun observeSavedTokenizationIDs(): Flow<List<String>> =
        cardDao.getAll().map { it.map { tokenizedCard -> tokenizedCard.tokenizationId } }

    fun getToken(tokenizationId: String): Flow<RequestStatus<TokenizationRequestResponse>> =
        flowApiRequest { tokenPaymentsApi.requestTokenForTokenizationId(tokenizationId) }
}
