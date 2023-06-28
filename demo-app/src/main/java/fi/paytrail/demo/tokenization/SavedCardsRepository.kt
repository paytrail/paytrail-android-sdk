package fi.paytrail.demo.tokenization

import fi.paytrail.demo.util.RequestStatus
import fi.paytrail.sdk.apiclient.apis.TokenPaymentsApi
import fi.paytrail.sdk.apiclient.models.ErrorResponse
import fi.paytrail.sdk.apiclient.models.TokenizationRequestResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository to manage saved token IDs
 */
@Singleton
class SavedCardsRepository @Inject constructor(
    private val tokenPaymentsApi: TokenPaymentsApi,
    private val cardDao: TokenizedCardDao,
) {

    // private val savedTokenizationIds = MutableStateFlow(emptyList<String>())

    suspend fun saveTokenizationId(tokenizationId: String) {
        cardDao.insertAll(TokenizedCard(tokenizationId))
        // savedTokenizationIds.value = (savedTokenizationIds.value + tokenizationId).distinct()
    }

    suspend fun removeTokenizationId(tokenizationId: String) {
        cardDao.delete(TokenizedCard(tokenizationId))
    }

    fun observeSavedTokenizationIDs(): Flow<List<String>> =
        cardDao.getAll().map { it.map { tokenizedCard -> tokenizedCard.tokenizationId } }

    fun getToken(tokenizationId: String): Flow<RequestStatus<TokenizationRequestResponse>> = flow {
        emit(RequestStatus.loading())
        val result = try {
            tokenPaymentsApi
                .requestTokenForTokenizationId(tokenizationId)
                .asRequestStatus()
        } catch (e: Exception) {
            RequestStatus.error(exception = e)
        }
        emit(result)
    }
}

private fun <T> Response<T>.asRequestStatus(): RequestStatus<T> {
    return if (isSuccessful) {
        RequestStatus.success(value = body())
    } else {
        RequestStatus.error(error = bodyAsErrorResponse())
    }
}

private fun <T> Response<T>.bodyAsErrorResponse(): ErrorResponse? {
    return errorBody()?.string()?.let {
        try {
            ErrorResponse.deserialize(it)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}
