package fi.paytrail.sdk.apiclient.apis

import fi.paytrail.sdk.apiclient.infrastructure.CollectionFormats.*
import fi.paytrail.sdk.apiclient.models.SettlementIdItem
import kotlinx.serialization.SerialName
import retrofit2.Response
import retrofit2.http.*

interface SettlementsApi {

    /**
     * List settlement IDs
     * Returns settlement IDs
     * Responses:
     *  - 200: List of settlement IDs
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 404: The specified resource was not found
     *  - 0: Unexpected error
     *
     * @param startDate Filter out settlements created before given date. (optional)
     * @param endDate Filter out settlements created after given date. (optional)
     * @param bankReference Only include settlements with the given bank reference (optional)
     * @param limit Limit the number of settlement IDs returned (optional)
     * @param submerchant Get submerchant&#39;s payment report (optional)
     * @return [kotlin.collections.List<SettlementIdItem>]
     */
    @GET("settlements")
    suspend fun listSettlementIds(
        @Query("startDate") startDate: java.time.LocalDate? = null,
        @Query("endDate") endDate: java.time.LocalDate? = null,
        @Query("bankReference") bankReference: kotlin.String? = null,
        @Query("limit") limit: kotlin.Int? = null,
        @Header("submerchant") submerchant: kotlin.Int? = null,
    ): Response<kotlin.collections.List<SettlementIdItem>>
}
