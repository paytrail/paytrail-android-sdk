
# PaymentReportRequest

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**requestType** | [**inline**](#RequestType) | In which format is the report delivered in | 
**callbackUrl** | **kotlin.String** | To which URL is the report delivered to. Callback URLs must use HTTPS | 
**paymentStatus** | [**inline**](#PaymentStatus) | How are the payments statuses filtered. \&quot;Default\&quot; includes both paid and settled payments. \&quot;Paid\&quot; includes paid payments that have not been settled yet. \&quot;All\&quot; includes everything, for example unpaid or failed payments. |  [optional]
**startDate** | **kotlin.String** | Filter out payments created before given datetime. |  [optional]
**endDate** | **kotlin.String** | Filter out payments created after given datetime. |  [optional]
**limit** | **kotlin.Int** | Limit the amount of payments included in the report. |  [optional]
**reportFields** | [**inline**](#kotlin.collections.List&lt;ReportFields&gt;) | Limit the amount of fields included in the report. By default all fields are included. |  [optional]
**submerchant** | **kotlin.Int** | Get submerchant&#39;s payment report |  [optional]


<a id="RequestType"></a>
## Enum: requestType
Name | Value
---- | -----
requestType | json, csv


<a id="PaymentStatus"></a>
## Enum: paymentStatus
Name | Value
---- | -----
paymentStatus | default, all, paid, settled


<a id="kotlin.collections.List<ReportFields>"></a>
## Enum: reportFields
Name | Value
---- | -----
reportFields | entryDate, created, amount, status, firstname, familyname, description, reference, paymentMethod, stamp, address, postcode, postoffice, country, checkoutReference, archiveNumber, payerName, settlementId, settlementDate, originalTradeReference, vatPercentage, vatAmount, paymentMethodFee, paymentMethodCommission, shopInShopCommission, shopInShopCommissionVatPercentage, shopInShopCommissionVatAmount, refunditems



