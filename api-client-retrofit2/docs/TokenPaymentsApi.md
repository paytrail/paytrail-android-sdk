# TokenPaymentsApi

All URIs are relative to *https://services.paytrail.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addCardForm**](TokenPaymentsApi.md#addCardForm) | **POST** tokenization/addcard-form | Request a redirect to card addition form
[**requestTokenForTokenizationId**](TokenPaymentsApi.md#requestTokenForTokenizationId) | **POST** tokenization/{checkout-tokenization-id} | Request a card token for given tokenization id
[**tokenCitAuthorizationHold**](TokenPaymentsApi.md#tokenCitAuthorizationHold) | **POST** payments/token/cit/authorization-hold | Request customer initiated transaction authorization hold on token
[**tokenCitCharge**](TokenPaymentsApi.md#tokenCitCharge) | **POST** payments/token/cit/charge | Request customer initiated transaction charge on token
[**tokenCommit**](TokenPaymentsApi.md#tokenCommit) | **POST** payments/{transactionId}/token/commit | Request committing (charging) of previously created authorization hold on token
[**tokenMitAuthorizationHold**](TokenPaymentsApi.md#tokenMitAuthorizationHold) | **POST** payments/token/mit/authorization-hold | Request merchant initiated transaction authorization hold on token
[**tokenMitCharge**](TokenPaymentsApi.md#tokenMitCharge) | **POST** payments/token/mit/charge | Request merchant initiated transaction charge on token
[**tokenRevert**](TokenPaymentsApi.md#tokenRevert) | **POST** payments/{transactionId}/token/revert | Revert (removal) of previously created authorization hold on token



Request a redirect to card addition form

Request a redirect to card addition form. This will redirect user to the card addition form, then back to Checkout servers, and finally to merchant&#39;s redirect url. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(TokenPaymentsApi::class.java)
val addCardFormRequest : AddCardFormRequest =  // AddCardFormRequest | Add card payload

launch(Dispatchers.IO) {
    webService.addCardForm(addCardFormRequest)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **addCardFormRequest** | [**AddCardFormRequest**](AddCardFormRequest.md)| Add card payload |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Request a card token for given tokenization id

Use checkout-tokenization-id received from /tokenization/addcard-form redirect to request a token which can be used for payments. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(TokenPaymentsApi::class.java)
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : TokenizationRequestResponse = webService.requestTokenForTokenizationId(checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, checkoutNonce, signature)
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

### Return type

[**TokenizationRequestResponse**](TokenizationRequestResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Request customer initiated transaction authorization hold on token

Request customer initiated transaction authorization hold on token. CIT authorization holds can sometimes require further 3DS authentication step-up. See detailed documentation at https://checkoutfinland.github.io/psp-api/#/?id&#x3D;customer-initiated-transactions-cit 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(TokenPaymentsApi::class.java)
val tokenPaymentRequest : TokenPaymentRequest =  // TokenPaymentRequest | CIT authorization hold payload
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : TokenMITPaymentResponse = webService.tokenCitAuthorizationHold(tokenPaymentRequest, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, checkoutNonce, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tokenPaymentRequest** | [**TokenPaymentRequest**](TokenPaymentRequest.md)| CIT authorization hold payload |
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **checkoutNonce** | **kotlin.String**| Unique random identifier | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**TokenMITPaymentResponse**](TokenMITPaymentResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Request customer initiated transaction charge on token

Request customer initiated transaction charge on token. CIT charges can sometimes require further 3DS authentication step-up. See detailed documentation at https://checkoutfinland.github.io/psp-api/#/?id&#x3D;customer-initiated-transactions-cit 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(TokenPaymentsApi::class.java)
val tokenPaymentRequest : TokenPaymentRequest =  // TokenPaymentRequest | CIT Charge payload
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : TokenMITPaymentResponse = webService.tokenCitCharge(tokenPaymentRequest, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, checkoutNonce, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tokenPaymentRequest** | [**TokenPaymentRequest**](TokenPaymentRequest.md)| CIT Charge payload |
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **checkoutNonce** | **kotlin.String**| Unique random identifier | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**TokenMITPaymentResponse**](TokenMITPaymentResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Request committing (charging) of previously created authorization hold on token

Request committing of previously created authorization hold. The final amount committed can either equal or be less than the authorization hold. The committed amount may not exceed the authorization hold. The final items may differ from the ones used when creating the authorization hold. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(TokenPaymentsApi::class.java)
val tokenPaymentRequest : TokenPaymentRequest =  // TokenPaymentRequest | CIT commit payload
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : TokenMITPaymentResponse = webService.tokenCommit(tokenPaymentRequest, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, checkoutNonce, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tokenPaymentRequest** | [**TokenPaymentRequest**](TokenPaymentRequest.md)| CIT commit payload |
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **checkoutNonce** | **kotlin.String**| Unique random identifier | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**TokenMITPaymentResponse**](TokenMITPaymentResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Request merchant initiated transaction authorization hold on token

Request merchant initiated transaction authorization hold on token. This method should be used when creating an authorization hold on the customer&#39;s card in a context, where the customer is not actively participating in the transaction. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(TokenPaymentsApi::class.java)
val tokenPaymentRequest : TokenPaymentRequest =  // TokenPaymentRequest | MIT Charge payload
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : TokenMITPaymentResponse = webService.tokenMitAuthorizationHold(tokenPaymentRequest, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, checkoutNonce, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tokenPaymentRequest** | [**TokenPaymentRequest**](TokenPaymentRequest.md)| MIT Charge payload |
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **checkoutNonce** | **kotlin.String**| Unique random identifier | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**TokenMITPaymentResponse**](TokenMITPaymentResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Request merchant initiated transaction charge on token

Request merchant initiated transaction charge on token. This method should be used when charging the customer&#39;s card in a context, where the customer is not actively participating in the transaction. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(TokenPaymentsApi::class.java)
val tokenPaymentRequest : TokenPaymentRequest =  // TokenPaymentRequest | MIT Charge payload
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : TokenMITPaymentResponse = webService.tokenMitCharge(tokenPaymentRequest, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, checkoutNonce, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tokenPaymentRequest** | [**TokenPaymentRequest**](TokenPaymentRequest.md)| MIT Charge payload |
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **checkoutNonce** | **kotlin.String**| Unique random identifier | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**TokenMITPaymentResponse**](TokenMITPaymentResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Revert (removal) of previously created authorization hold on token

Request committing of previously created authorization hold. A successful revert will remove the authorization hold from the payer&#39;s bank account. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(TokenPaymentsApi::class.java)
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val checkoutNonce : kotlin.String = 39da40b8-5fb0-499c-869d-35e575b9a6cd // kotlin.String | Unique random identifier
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : TokenMITPaymentResponse = webService.tokenRevert(checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, checkoutNonce, signature)
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

### Return type

[**TokenMITPaymentResponse**](TokenMITPaymentResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

