package fi.paytrail.sdk.apiclient.apis

import fi.paytrail.sdk.apiclient.models.PaymentReportBySettlementIdRequest
import fi.paytrail.sdk.apiclient.models.PaymentReportRequest
import fi.paytrail.sdk.apiclient.models.PaymentReportRequestResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentReportsApi {

    /**
     * Request a payment report
     * Request a report of payments to be sent to the given callback url. Callback schema defined in PaymentReportCallbackJSON.
     * Responses:
     *  - 200: Payment report request response
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 404: The specified resource was not found
     *  - 0: Unexpected error
     *
     * @param paymentReportRequest Payment body payload
     * @return [PaymentReportRequestResponse]
     */
    @POST("payments/report")
    suspend fun requestPaymentReport(
        @Body paymentReportRequest: PaymentReportRequest,
    ): Response<PaymentReportRequestResponse>

    /**
     * Request a report of payments contained in a given settlement
     * Request a report of payments contained in a given settlement to be sent to the given callback url. Callback schema defined in PaymentReportCallbackJSON.
     * Responses:
     *  - 200: Payment report request response
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 404: The specified resource was not found
     *  - 0: Unexpected error
     *
     * @param settlementId Settlement ID
     * @param paymentReportBySettlementIdRequest Payment body payload
     * @return [PaymentReportRequestResponse]
     */
    @POST("settlements/{settlementId}/payments/report")
    suspend fun requestPaymentReportBySettlementId(
        @Path("settlementId") settlementId: String,
        @Body paymentReportBySettlementIdRequest: PaymentReportBySettlementIdRequest,
    ): Response<PaymentReportRequestResponse>
}
