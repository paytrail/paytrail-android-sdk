
# Payment

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**status** | [**inline**](#Status) | Transaction status | 
**amount** | **kotlin.Long** |  | 
**currency** | [**inline**](#Currency) |  | 
**stamp** | **kotlin.String** |  | 
**reference** | **kotlin.String** |  | 
**createdAt** | **kotlin.String** |  | 
**transactionId** | [**java.util.UUID**](java.util.UUID.md) | Transaction ID |  [optional]
**href** | **kotlin.String** | If transaction is in status &#39;new&#39;, link to the hosted payment gateway |  [optional]
**provider** | **kotlin.String** | If processed, the name of the payment method provider |  [optional]
**filingCode** | **kotlin.String** | If paid, the filing code issued by the payment method provider if any |  [optional]
**paidAt** | **kotlin.String** | Timestamp when the transaction was paid |  [optional]


<a id="Status"></a>
## Enum: status
Name | Value
---- | -----
status | new, ok, fail, pending, delayed


<a id="Currency"></a>
## Enum: currency
Name | Value
---- | -----
currency | EUR



