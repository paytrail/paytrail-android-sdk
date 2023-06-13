
# Refund

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**callbackUrls** | [**Callbacks**](Callbacks.md) |  | 
**amount** | **kotlin.Long** | The amount to refund. Required for normal merchant accounts. SiS aggregate can refund the whole purchase by providing just the amount  |  [optional]
**refundStamp** | **kotlin.String** | Merchant specific unique stamp for the refund |  [optional]
**refundReference** | **kotlin.String** | Merchant reference for the refund |  [optional]
**items** | [**kotlin.collections.List&lt;RefundItem&gt;**](RefundItem.md) | Item level refund information for SiS refunds. |  [optional]



