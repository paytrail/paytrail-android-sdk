# PaymentReportsApi

All URIs are relative to *https://services.paytrail.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**requestPaymentReport**](PaymentReportsApi.md#requestPaymentReport) | **POST** payments/report | Request a payment report
[**requestPaymentReportBySettlementId**](PaymentReportsApi.md#requestPaymentReportBySettlementId) | **POST** settlements/{settlementId}/payments/report | Request a report of payments contained in a given settlement



Request a payment report

Request a report of payments to be sent to the given callback url. Callback schema defined in PaymentReportCallbackJSON. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(PaymentReportsApi::class.java)
val paymentReportRequest : PaymentReportRequest =  // PaymentReportRequest | Payment body payload
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : PaymentReportRequestResponse = webService.requestPaymentReport(paymentReportRequest, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **paymentReportRequest** | [**PaymentReportRequest**](PaymentReportRequest.md)| Payment body payload |
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**PaymentReportRequestResponse**](PaymentReportRequestResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Request a report of payments contained in a given settlement

Request a report of payments contained in a given settlement to be sent to the given callback url. Callback schema defined in PaymentReportCallbackJSON. 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(PaymentReportsApi::class.java)
val settlementId : kotlin.String = 2397548234 // kotlin.String | Settlement ID
val paymentReportBySettlementIdRequest : PaymentReportBySettlementIdRequest =  // PaymentReportBySettlementIdRequest | Payment body payload
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : PaymentReportRequestResponse = webService.requestPaymentReportBySettlementId(settlementId, paymentReportBySettlementIdRequest, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **settlementId** | **kotlin.String**| Settlement ID |
 **paymentReportBySettlementIdRequest** | [**PaymentReportBySettlementIdRequest**](PaymentReportBySettlementIdRequest.md)| Payment body payload |
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**PaymentReportRequestResponse**](PaymentReportRequestResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

