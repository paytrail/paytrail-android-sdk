
# PaymentReportBySettlementIdRequest

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**requestType** | [**inline**](#RequestType) | In which format is the report delivered in | 
**callbackUrl** | **kotlin.String** | To which URL is the report delivered to | 
**reportFields** | [**inline**](#kotlin.collections.List&lt;ReportFields&gt;) | Limit the amount of fields included in the report. By default all fields are included. |  [optional]
**submerchant** | **kotlin.Int** | Get submerchant&#39;s payment report |  [optional]


<a id="RequestType"></a>
## Enum: requestType
Name | Value
---- | -----
requestType | json, csv


<a id="kotlin.collections.List<ReportFields>"></a>
## Enum: reportFields
Name | Value
---- | -----
reportFields | entryDate, created, amount, status, firstname, familyname, description, reference, paymentMethod, stamp, address, postcode, postoffice, country, checkoutReference, archiveNumber, payerName, settlementId, settlementDate, originalTradeReference, vatPercentage, vatAmount, paymentMethodFee, paymentMethodCommission, shopInShopCommission, shopInShopCommissionVatPercentage, shopInShopCommissionVatAmount



