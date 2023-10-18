package fi.paytrail.sdk.apiclient
/**
 * Represents the merchant's account details in the Paytrail system.
 *
 * This data class is essential for initializing various operations within the Paytrail SDK,
 * as it provides the necessary credentials to authenticate and authorize transactions.
 *
 * @param id The unique identifier associated with the merchant's account.
 * @param secret The confidential key/string associated with the merchant's account,
 *               used for secure operations and verifications.
 */
data class MerchantAccount(
    val id: Int,
    val secret: String,
)
