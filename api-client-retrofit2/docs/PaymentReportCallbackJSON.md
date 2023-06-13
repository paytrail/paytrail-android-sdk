
# PaymentReportCallbackJSON

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**entryDate** | **kotlin.String** | When the payment was paid or initialized |  [optional]
**created** | **kotlin.String** | Hour and minute of the payment creation |  [optional]
**amount** | **kotlin.Float** | Payment amount in Euros |  [optional]
**status** | [**inline**](#Status) | Payment status |  [optional]
**firstname** | **kotlin.String** | First name |  [optional]
**familyname** | **kotlin.String** | Last name |  [optional]
**description** | **kotlin.String** | Payment description |  [optional]
**reference** | **kotlin.String** | Merchant&#39;s reference |  [optional]
**paymentMethod** | **kotlin.String** | Payment method |  [optional]
**stamp** | **kotlin.String** | Payment Stamp |  [optional]
**address** | **kotlin.String** | Delivery address |  [optional]
**postcode** | **kotlin.String** | Postal code |  [optional]
**postoffice** | **kotlin.String** | Post office |  [optional]
**country** | **kotlin.String** | Country of delivery |  [optional]
**checkoutReference** | **kotlin.String** | Checkout reference |  [optional]
**archiveNumber** | **kotlin.String** | Archive number |  [optional]
**payerName** | **kotlin.String** | Name from bank |  [optional]
**settlementId** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | Settlement (report) id |  [optional]
**settlementDate** | **kotlin.String** | When the payment is settled |  [optional]
**originalTradeReference** | **kotlin.String** | Merchant&#39;s reference of the original payment in case of refund |  [optional]
**vatPercentage** | **kotlin.Float** | Payment VAT percentage |  [optional]
**vatAmount** | **kotlin.Float** | Payment VAT amount in Euros |  [optional]
**paymentMethodFee** | **kotlin.Float** | Payment method fee in Euros |  [optional]
**paymentMethodCommission** | **kotlin.Float** | Payment method commission amount in Euros |  [optional]
**shopInShopCommission** | **kotlin.Float** | Payment method commission amount in Euros |  [optional]
**shopInShopCommissionVatPercentage** | **kotlin.Float** | Payment method commission VAT percentage |  [optional]
**shopInShopCommissionVatAmount** | **kotlin.Float** | Payment method commission VAT amount in Euros |  [optional]
**refunditems** | **kotlin.String** | Contains a &#x60;|&#x60; (pipe) -delimited list of *stamp*:*amount* pairs. Pairs are delimited by a &#x60;:&#x60; (colon). Stamp refers to the payment item&#39;s stamp that was refunded. |  [optional]


<a id="Status"></a>
## Enum: status
Name | Value
---- | -----
status | New, Paid, waiting for approval, Cancelled, Timeout, Halted, Delayed, Paid and settled



