package fi.paytrail.sdk.apiclient.infrastructure

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class PaytrailCallTimestampInjector : HeaderInjectingInterceptor(
    "checkout-timestamp",
    { DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now()) },
)
