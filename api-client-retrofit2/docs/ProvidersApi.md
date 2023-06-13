# ProvidersApi

All URIs are relative to *https://services.paytrail.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getGroupedPaymentProviders**](ProvidersApi.md#getGroupedPaymentProviders) | **GET** merchants/grouped-payment-providers | List grouped merchant payment methods
[**getPaymentProviders**](ProvidersApi.md#getPaymentProviders) | **GET** merchants/payment-providers | List merchant payment methods



List grouped merchant payment methods

Similar to the /merchants/payment-providers, but in addition of a flat list of providers, it returns payment group data containing localized names, icon URLs and grouped providers, and a localized text with a link to the terms of payment. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(ProvidersApi::class.java)
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


List merchant payment methods

Returns the payment methods available for merchant without creating a new payment to the system. Useful for displaying payment provider icons during different phases of checkout before finally actually creating the payment request when customer decides to pay 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(ProvidersApi::class.java)
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

