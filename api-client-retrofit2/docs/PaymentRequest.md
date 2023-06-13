
# PaymentRequest

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**stamp** | **kotlin.String** | Merchant specific unique stamp | 
**reference** | **kotlin.String** | Merchant reference for the payment | 
**amount** | **kotlin.Long** | Total amount of the payment (sum of items), VAT should be included in amount unless &#x60;usePricesWithoutVat&#x60; is set to true | 
**currency** | [**inline**](#Currency) |  | 
**language** | [**inline**](#Language) | Alpha-2 language code for the payment process | 
**items** | [**kotlin.collections.List&lt;Item&gt;**](Item.md) |  | 
**customer** | [**Customer**](Customer.md) |  | 
**redirectUrls** | [**Callbacks**](Callbacks.md) |  | 
**orderId** | **kotlin.String** | Order ID. Used for eg. Collector payments order ID. If not given, merchant reference is used instead. |  [optional]
**deliveryAddress** | [**Address**](Address.md) |  |  [optional]
**invoicingAddress** | [**Address**](Address.md) |  |  [optional]
**manualInvoiceActivation** | **kotlin.Boolean** | If paid with invoice payment method, the invoice will not be activated automatically immediately. Currently only supported with Collector. |  [optional]
**callbackUrls** | [**Callbacks**](Callbacks.md) |  |  [optional]
**callbackDelay** | **kotlin.Int** | Callback delay in seconds. If callback URLs and delay are provided, callbacks will be called after the delay. |  [optional]
**groups** | [**inline**](#kotlin.collections.List&lt;Groups&gt;) | Optionally return only payment methods for selected groups |  [optional]
**usePricesWithoutVat** | **kotlin.Boolean** | If true, &#x60;amount&#x60; and &#x60;items.unitPrice&#x60; should be sent to API without VAT, and final VAT-included prices are calculated by Paytrail&#39;s system (with prices rounded to closest cent). Also, when true, items must be included. |  [optional]


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


<a id="kotlin.collections.List<Groups>"></a>
## Enum: groups
Name | Value
---- | -----
groups | mobile, bank, creditcard, credit, other



