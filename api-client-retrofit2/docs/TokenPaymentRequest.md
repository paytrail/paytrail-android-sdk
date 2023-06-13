
# TokenPaymentRequest

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**stamp** | **kotlin.String** | Merchant specific unique stamp | 
**reference** | **kotlin.String** | Merchant reference for the payment | 
**amount** | **kotlin.Long** | Total amount of the payment (sum of items), VAT included | 
**currency** | [**inline**](#Currency) |  | 
**language** | [**inline**](#Language) | Alpha-2 language code for the payment process | 
**items** | [**kotlin.collections.List&lt;Item&gt;**](Item.md) |  | 
**customer** | [**Customer**](Customer.md) |  | 
**redirectUrls** | [**Callbacks**](Callbacks.md) |  | 
**token** | **kotlin.String** | Payment card token received from request to /tokenization/{checkout-tokenization-id} | 
**orderId** | **kotlin.String** | Order ID. Used for eg. Collector payments order ID. If not given, merchant reference is used instead. |  [optional]
**deliveryAddress** | [**Address**](Address.md) |  |  [optional]
**invoicingAddress** | [**Address**](Address.md) |  |  [optional]
**callbackUrls** | [**Callbacks**](Callbacks.md) |  |  [optional]
**callbackDelay** | **kotlin.Int** | Callback delay in seconds. If callback URLs and delay are provided, callbacks will be called after the delay. |  [optional]


<a id="Currency"></a>
## Enum: currency
Name | Value
---- | -----
currency | EUR


<a id="Language"></a>
## Enum: language
Name | Value
---- | -----
language | FI, SV, EN



