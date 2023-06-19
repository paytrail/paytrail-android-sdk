package fi.paytrail.sdk.apiclient

data class MerchantAccount(
    val id: Int,
    val secret: String,
) {

    companion object {
        private val UNDEFINED = MerchantAccount(0, "")

        // XXX: Remove default MerchantAccount, and require it always as parameter to payment flow?
        var default: MerchantAccount = UNDEFINED
            get() {
                if (field == UNDEFINED) {
                    throw RuntimeException("Default merchant configuration not set up")
                }
                return field
            }
    }
}
