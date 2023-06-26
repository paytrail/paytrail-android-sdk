package fi.paytrail.paymentsdk.model

import fi.paytrail.sdk.apiclient.models.ErrorResponse

data class PaytrailApiErrorResponse(
    val code: Int,
    val errorBody: String? = null,
    val errorResponse: ErrorResponse? = null,
)
