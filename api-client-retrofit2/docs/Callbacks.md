
# Callbacks

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**success** | **kotlin.String** | URL to call when payment is succesfully paid. Can called multiple times; one must ensure idempotency of this endpoint.  | 
**cancel** | **kotlin.String** | URL to call when payment is cancelled and not fulfilled. Can called multiple times; one must ensure idempotency of this endpoint.  | 



