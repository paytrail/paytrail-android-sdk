
# Item

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**unitPrice** | **kotlin.Long** | Unit price of an item in currency minor unit, eg. EUR cents. VAT should be included in amount unless &#x60;usePricesWithoutVat&#x60; is set to true. | 
**units** | **kotlin.Long** | Number of units | 
**vatPercentage** | **kotlin.Long** | Item VAT percentage | 
**productCode** | **kotlin.String** | Merchant specific product code | 
**deliveryDate** | [**java.time.LocalDate**](java.time.LocalDate.md) | Estimated delivery date |  [optional]
**description** | **kotlin.String** | Merchant specific product description |  [optional]
**category** | **kotlin.String** | Item product category |  [optional]
**merchant** | **kotlin.String** | Submerchant ID. Required for shop-in-shop payments, leave out from normal payments. |  [optional]
**stamp** | **kotlin.String** | Submerchant specific unique stamp. Required for shop-in-shop payments, leave out from normal payments. |  [optional]
**reference** | **kotlin.String** | Submerchant reference for the item. Required for shop-in-shop payments, leave out from normal payments. |  [optional]
**orderId** | **kotlin.String** | Order ID. Used for eg. Collector payments order ID. If not given, merchant reference is used instead. |  [optional]
**commission** | [**ItemCommission**](ItemCommission.md) |  |  [optional]



