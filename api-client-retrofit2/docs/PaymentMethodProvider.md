
# PaymentMethodProvider

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **kotlin.String** | ID of the provider | 
**name** | **kotlin.String** | Display name of the payment method | 
**svg** | **kotlin.String** | URL to payment method SVG icon (recommended to be used instead if PNG) | 
**icon** | **kotlin.String** | URL to payment method PNG icon | 
**group** | [**inline**](#Group) |  | 
**url** | **kotlin.String** | Form action url | 
**parameters** | [**kotlin.collections.List&lt;PaymentMethodProviderAllOfParameters&gt;**](PaymentMethodProviderAllOfParameters.md) |  | 


<a id="Group"></a>
## Enum: group
Name | Value
---- | -----
group | mobile, bank, creditcard, credit



