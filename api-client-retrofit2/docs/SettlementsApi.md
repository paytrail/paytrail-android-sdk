# SettlementsApi

All URIs are relative to *https://services.paytrail.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**listSettlementIds**](SettlementsApi.md#listSettlementIds) | **GET** settlements | List settlement IDs



List settlement IDs

Returns settlement IDs 

### Example
```kotlin
// Import classes:
//import fi.paytrail.sdk.apiclient.*
//import fi.paytrail.sdk.apiclient.infrastructure.*
//import fi.paytrail.sdk.apiclient.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SettlementsApi::class.java)
val startDate : java.time.LocalDate = 2019-01-01 // java.time.LocalDate | Filter out settlements created before given date.
val endDate : java.time.LocalDate = 2019-01-01 // java.time.LocalDate | Filter out settlements created after given date.
val bankReference : kotlin.String = 1234567890-1111 // kotlin.String | Only include settlements with the given bank reference
val limit : kotlin.Int = 1 // kotlin.Int | Limit the number of settlement IDs returned
val submerchant : kotlin.Int = 695874 // kotlin.Int | Get submerchant's payment report
val checkoutAccount : kotlin.Int = 375917 // kotlin.Int | Merchant ID
val checkoutAlgorithm : kotlin.String = sha512 // kotlin.String | HMAC algorithm
val checkoutMethod : kotlin.String = POST // kotlin.String | HTTP method of the request
val checkoutTimestamp : java.time.OffsetDateTime = 2018-08-08T10:10:59Z // java.time.OffsetDateTime | Current timestamp in ISO 8601 format
val signature : kotlin.String = signature_example // kotlin.String | HMAC signature calculated over the request headers and payload

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<SettlementIdItem> = webService.listSettlementIds(startDate, endDate, bankReference, limit, submerchant, checkoutAccount, checkoutAlgorithm, checkoutMethod, checkoutTimestamp, signature)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **startDate** | **java.time.LocalDate**| Filter out settlements created before given date. | [optional]
 **endDate** | **java.time.LocalDate**| Filter out settlements created after given date. | [optional]
 **bankReference** | **kotlin.String**| Only include settlements with the given bank reference | [optional]
 **limit** | **kotlin.Int**| Limit the number of settlement IDs returned | [optional]
 **submerchant** | **kotlin.Int**| Get submerchant&#39;s payment report | [optional]
 **checkoutAccount** | **kotlin.Int**| Merchant ID | [optional]
 **checkoutAlgorithm** | **kotlin.String**| HMAC algorithm | [optional] [enum: sha256, sha512]
 **checkoutMethod** | **kotlin.String**| HTTP method of the request | [optional] [enum: GET, POST]
 **checkoutTimestamp** | **java.time.OffsetDateTime**| Current timestamp in ISO 8601 format | [optional]
 **signature** | **kotlin.String**| HMAC signature calculated over the request headers and payload | [optional]

### Return type

[**kotlin.collections.List&lt;SettlementIdItem&gt;**](SettlementIdItem.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

