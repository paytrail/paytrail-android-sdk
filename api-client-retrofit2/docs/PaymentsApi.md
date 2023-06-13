# PaymentsApi

All URIs are relative to *https://services.paytrail.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**activateInvoiceByTransactionId**](PaymentsApi.md#activateInvoiceByTransactionId) | **POST** payments/{transactionId}/activate-invoice | Activate invoice
[**createPayment**](PaymentsApi.md#createPayment) | **POST** payments | Create a new open payment
[**getGroupedPaymentProviders**](PaymentsApi.md#getGroupedPaymentProviders) | **GET** merchants/grouped-payment-providers | List grouped merchant payment methods
[**getPaymentByTransactionId**](PaymentsApi.md#getPaymentByTransactionId) | **GET** payments/{transactionId} | Get a payment by Checkout transaction ID
[**getPaymentProviders**](PaymentsApi.md#getPaymentProviders) | **GET** merchants/payment-providers | List merchant payment methods
[**refundPaymentByTransactionId**](PaymentsApi.md#refundPaymentByTransactionId) | **POST** payments/{transactionId}/refund | Refund a payment



Activate invoice

Manually activate invoice by transaction ID. Can only be used if payment was paid with Collector and is in pending status. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(PaymentsApi::class.java)
val transactionId : java.util.UUID = 93ee8d18-10db-410b-92ac-7d6e49369ce3 // java.util.UUID | Transaction ID of payment to activate invoice
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTransactionId : java.util.UUID = 93ee8d18-10db-410b-92ac-7d6e49369ce3 // java.util.UUID | The same transaction ID as in route
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : ActivateInvoiceResponse = webService.activateInvoiceByTransactionId(transactionId, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTransactionId, checkoutTimestamp, checkoutNonce, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **transactionId** | **java.util.UUID**| Transaction ID of payment to activate invoice |
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTransactionId** | **java.util.UUID**| The same transaction ID as in route | [optional]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **checkoutNonce** | **kotlin.String**| Unique random identifier | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**ActivateInvoiceResponse**](ActivateInvoiceResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Create a new open payment

Create a new open payment, returns a list of available payment methods.

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(PaymentsApi::class.java)
val paymentRequest : PaymentRequest =  // PaymentRequest | Payment body payload
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : PaymentRequestResponse = webService.createPayment(paymentRequest, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, checkoutNonce, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **paymentRequest** | [**PaymentRequest**](PaymentRequest.md)| Payment body payload |
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **checkoutNonce** | **kotlin.String**| Unique random identifier | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**PaymentRequestResponse**](PaymentRequestResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


List grouped merchant payment methods

Similar to the /merchants/payment-providers, but in addition of a flat list of providers, it returns payment group data containing localized names, icon URLs and grouped providers, and a localized text with a link to the terms of payment. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(PaymentsApi::class.java)
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = GET // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload
val amount : kotlin.Int = 500 // kotlin.Int | Optional amount in minor unit (eg. EUR cents) for the payment providers. Some providers have minimum or maximum amounts that can be purchased. 
val groups : kotlin.collections.List<kotlin.String> =  // kotlin.collections.List<kotlin.String> | Comma separated list of payment method groups to include in the reply.
val language : kotlin.String = language_example // kotlin.String | Language code of the language the terms of payment and the payment group names will be localized in. Defaults to FI if left undefined 

launch(Dispatchers.IO) {
    val result : GroupedPaymentProvidersResponse = webService.getGroupedPaymentProviders(checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, checkoutNonce, signature, amount, groups, language)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **checkoutNonce** | **kotlin.String**| Unique random identifier | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]
 **amount** | **kotlin.Int**| Optional amount in minor unit (eg. EUR cents) for the payment providers. Some providers have minimum or maximum amounts that can be purchased.  | [optional]
 **groups** | [**kotlin.collections.List&lt;kotlin.String&gt;**](kotlin.String.md)| Comma separated list of payment method groups to include in the reply. | [optional] [enum: mobile, bank, creditcard, credit, other]
 **language** | **kotlin.String**| Language code of the language the terms of payment and the payment group names will be localized in. Defaults to FI if left undefined  | [optional] [enum: FI, SV, EN]

### Return type

[**GroupedPaymentProvidersResponse**](GroupedPaymentProvidersResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Get a payment by Checkout transaction ID

Get a single payment

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(PaymentsApi::class.java)
val transactionId : java.util.UUID = 93ee8d18-10db-410b-92ac-7d6e49369ce3 // java.util.UUID | Transaction ID of payment to fetch
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = GET // kotlin.String | HTTP method of the request
val checkoutTransactionId : java.util.UUID = 93ee8d18-10db-410b-92ac-7d6e49369ce3 // java.util.UUID | The same transaction ID as in route
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : Payment = webService.getPaymentByTransactionId(transactionId, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTransactionId, checkoutTimestamp, checkoutNonce, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **transactionId** | **java.util.UUID**| Transaction ID of payment to fetch |
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTransactionId** | **java.util.UUID**| The same transaction ID as in route | [optional]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **checkoutNonce** | **kotlin.String**| Unique random identifier | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**Payment**](Payment.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


List merchant payment methods

Returns the payment methods available for merchant without creating a new payment to the system. Useful for displaying payment provider icons during different phases of checkout before finally actually creating the payment request when customer decides to pay 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(PaymentsApi::class.java)
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = GET // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload
val amount : kotlin.Int = 500 // kotlin.Int | Optional amount in minor unit (eg. EUR cents) for the payment providers. Some providers have minimum or maximum amounts that can be purchased. 
val groups : kotlin.collections.List<kotlin.String> =  // kotlin.collections.List<kotlin.String> | Comma separated list of payment method groups to include in the reply.

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<BasePaymentMethodProvider> = webService.getPaymentProviders(checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, checkoutNonce, signature, amount, groups)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **checkoutNonce** | **kotlin.String**| Unique random identifier | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]
 **amount** | **kotlin.Int**| Optional amount in minor unit (eg. EUR cents) for the payment providers. Some providers have minimum or maximum amounts that can be purchased.  | [optional]
 **groups** | [**kotlin.collections.List&lt;kotlin.String&gt;**](kotlin.String.md)| Comma separated list of payment method groups to include in the reply. | [optional] [enum: mobile, bank, creditcard, credit, other]

### Return type

[**kotlin.collections.List&lt;BasePaymentMethodProvider&gt;**](BasePaymentMethodProvider.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Refund a payment

Refund a payment by transaction ID. Refund operation is asynchronous. Refund request is validated, and if the refund can be done a 201 is returned. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(PaymentsApi::class.java)
val transactionId : java.util.UUID = 93ee8d18-10db-410b-92ac-7d6e49369ce3 // java.util.UUID | Transaction ID of payment to refund
val refund : Refund =  // Refund | Refund payload
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTransactionId : java.util.UUID = 93ee8d18-10db-410b-92ac-7d6e49369ce3 // java.util.UUID | The same transaction ID as in route
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : RefundResponse = webService.refundPaymentByTransactionId(transactionId, refund, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTransactionId, checkoutTimestamp, checkoutNonce, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **transactionId** | **java.util.UUID**| Transaction ID of payment to refund |
 **refund** | [**Refund**](Refund.md)| Refund payload |
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTransactionId** | **java.util.UUID**| The same transaction ID as in route | [optional]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **checkoutNonce** | **kotlin.String**| Unique random identifier | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**RefundResponse**](RefundResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

