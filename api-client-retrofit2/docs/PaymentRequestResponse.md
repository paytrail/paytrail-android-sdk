
# PaymentRequestResponse

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**transactionId** | [**java.util.UUID**](java.util.UUID.md) | Checkout assigned transaction ID for the payment. | 
**href** | **kotlin.String** | Unique URL to hosted payment gateway | 
**terms** | **kotlin.String** | Text containing a link to the terms of payment |  [optional]
**groups** | [**kotlin.collections.List&lt;PaymentMethodGroupData&gt;**](PaymentMethodGroupData.md) | Contains data about the payment method groups. Contains only the groups found in the response&#39;s providers. |  [optional]
**providers** | [**kotlin.collections.List&lt;PaymentMethodProvider&gt;**](PaymentMethodProvider.md) |  |  [optional]



