
# Card

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**countryCode** | **kotlin.String** | e.g. FI | 
**type** | **kotlin.String** | Card type, for example ‘Visa’ |  [optional]
**partialPan** | **kotlin.String** | Last four digits of the card |  [optional]
**expireYear** | **kotlin.String** | Card expiration year |  [optional]
**expireMonth** | **kotlin.String** | Card expiration month |  [optional]
**cvcRequired** | **kotlin.String** | Whether the CVC is required for paying with this card. Can be one of yes, no or not_tested. |  [optional]
**bin** | **kotlin.String** | First 2 or 6 digits of the card number. (6 MC/VISA, 2 Amex/Diners) |  [optional]
**funding** | **kotlin.String** | credit, debit or unknown |  [optional]
**category** | **kotlin.String** | business, prepaid or unknown |  [optional]
**cardFingerprint** | **kotlin.String** | Identifies a specific card number. Cards with the same PAN but different expiry dates will have the same PAN fingerprint. Hex string of length 64. |  [optional]
**panFingerprint** | **kotlin.String** | Identifies a specific card, including the expiry date. Hex string of length 64. |  [optional]



