package fi.paytrail.demo.payments

import fi.paytrail.paymentsdk.RequestStatus
import fi.paytrail.paymentsdk.flowApiRequest
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.tokenization.asTokenizedPaymentRequest
import fi.paytrail.sdk.apiclient.apis.PaymentsApi
import fi.paytrail.sdk.apiclient.apis.TokenPaymentsApi
import fi.paytrail.sdk.apiclient.models.Payment
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import fi.paytrail.sdk.apiclient.models.TokenPaymentRequest
import fi.paytrail.sdk.apiclient.models.TokenPaymentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class PaymentData(
    val paymentId: UUID,
    val paymentRequest: PaymentRequest? = null,
    val token: String? = null,
    val state: PaytrailPaymentState? = null,
) {
    val transactionId: UUID? = state?.transactionId

    val tokenPaymentRequest: TokenPaymentRequest?
        get() = token?.let { paymentRequest?.asTokenizedPaymentRequest(it) }
}

@Singleton
class PaymentRepository @Inject constructor(
    private val paymentsApi: PaymentsApi,
    private val tokenPaymentsApi: TokenPaymentsApi,
) {

    private val paymentsStore = MutableStateFlow<Map<UUID, PaymentData>>(emptyMap())

    fun store(paymentId: UUID, paymentRequest: PaymentRequest) {
        val values = paymentsStore.value
        val old = values[paymentId]
        val new = old
            ?.copy(paymentRequest = paymentRequest)
            ?: PaymentData(
                paymentId = paymentId,
                paymentRequest = paymentRequest,
            )

        paymentsStore.value += (paymentId to new)
    }

    fun storeToken(paymentId: UUID, token: String) {
        val values = paymentsStore.value
        val new = values[paymentId]
            ?.copy(token = token)
            ?: PaymentData(
                paymentId = paymentId,
                token = token,
            )

        paymentsStore.value += (paymentId to new)
    }

    fun store(paymentId: UUID, state: PaytrailPaymentState) {
        val values = paymentsStore.value
        val new = values[paymentId]
            ?.copy(state = state)
            ?: PaymentData(
                paymentId = paymentId,
                state = state,
            )

        paymentsStore.value += (paymentId to new)
    }

    fun payments(): Flow<List<PaymentData>> = paymentsStore.map { it.values.toList() }

    fun getPaymentDetails(paymentId: UUID): Flow<RequestStatus<Payment>> {
        val transactionId = paymentsStore.value[paymentId]?.transactionId
        return if (transactionId != null) {
            flowApiRequest { paymentsApi.getPaymentByTransactionId(transactionId) }
        } else {
            flow { emit(RequestStatus.error(exception = IllegalArgumentException("No transaction ID available for payment $paymentId"))) }
        }
    }

    suspend fun commit(
        paymentId: UUID,
        tokenPaymentRequest: TokenPaymentRequest? = paymentsStore.value[paymentId]?.tokenPaymentRequest,
    ): Flow<RequestStatus<TokenPaymentResponse>> {
        val transactionId = paymentsStore.value[paymentId]?.transactionId
        return when {
            transactionId == null -> flow {
                emit(RequestStatus.error(exception = IllegalArgumentException("No transaction ID available for commit")))
            }

            tokenPaymentRequest == null -> flow {
                emit(
                    RequestStatus.error(
                        exception = IllegalArgumentException(
                            "No TokenPaymentRequest available for commit",
                        ),
                    ),
                )
            }

            else -> flowApiRequest {
                tokenPaymentsApi.tokenCommit(
                    transactionId = transactionId,
                    tokenPaymentRequest = tokenPaymentRequest,
                )
            }
        }
    }

    suspend fun revert(paymentId: UUID): Flow<RequestStatus<TokenPaymentResponse>> {
        val transactionId = paymentsStore.value[paymentId]?.transactionId
        return if (transactionId != null) {
            flowApiRequest { tokenPaymentsApi.tokenRevert(transactionId = transactionId) }
        } else {
            flow { emit(RequestStatus.error(exception = IllegalArgumentException("No transaction ID available for revert"))) }
        }
    }

    fun getPaymentData(id: UUID): Flow<PaymentData?> {
        return paymentsStore.map { it[id] }
    }
}
