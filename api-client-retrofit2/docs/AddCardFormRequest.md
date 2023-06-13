
# AddCardFormRequest

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**checkoutAccount** | **kotlin.Int** | Merchant ID | 
**checkoutAlgorithm** | [**inline**](#CheckoutAlgorithm) | HMAC algorithm | 
**checkoutRedirectSuccessUrl** | **kotlin.String** | Merchant&#39;s url for user redirect on successful card addition | 
**checkoutRedirectCancelUrl** | **kotlin.String** | Merchant&#39;s url for user redirect on failed card addition | 
**checkoutCallbackSuccessUrl** | **kotlin.String** | Merchant&#39;s url called on successful card addition |  [optional]
**checkoutCallbackCancelUrl** | **kotlin.String** | Merchant&#39;s url called on failed card addition |  [optional]
**language** | [**inline**](#Language) | Alpha-2 language code for the card addition form |  [optional]


<a id="CheckoutAlgorithm"></a>
## Enum: checkout-algorithm
Name | Value
---- | -----
checkoutAlgorithm | sha256, sha512


<a id="Language"></a>
## Enum: language
Name | Value
---- | -----
language | FI, SV, EN



