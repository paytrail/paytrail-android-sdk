

# paytrail-android-sdk
The Paytrail Android SDK provides pre-built solutions that simplify payment integration in your app. Our SDK offers flexibility - use just the payment API definitions with your custom views, or take advantage of our complete SDK experience with our ready-to-use components.

## Getting started
Our MSDK comprises two modules:
 1.  `api-client-retrofit2` - Responsible for server-side operations and hosting all Paytrail API definitions.
 2.  `payment-sdk` - Contains ready-to-use views and implements calls from the `api-client-retrofit2` module.

If your app has specific network configurations, logging mechanisms, or security requirements, our SDK easily integrates them. But, if you have no such specifications, our SDK uses a default client for smooth integration.
For the SDK to utilize your http client, initialize it through your Application class:

```kotlin
override fun onCreate() {  
    super.onCreate() 
    //Your own httpclient 
	val okHttpClientBuilder = OkHttpClient.Builder()  
 
	// Customize your own httpclient   
	val httpLogger = HttpLoggingInterceptor { Log.i("OkHttp", it) }  
	httpLogger.level = if (BuildConfig.DEBUG) BODY else BASIC  
	okHttpClientBuilder.addInterceptor(httpLogger)
	}
	//Then assign it to our sdk
	PaytrailBaseOkHttpClient.install(okHttpClientBuilder.build())  
}
````

## What does the API client module contain?
**Payment APIs**

| Method | API Call | Description | Parameters | Response Type |
|--------|---------------------------------------------------|-------------------------------------------------------------------------------------------------------|-------------------------------------------------------|---------------------|
| `POST` |  `createPayment(@Body paymentRequest: PaymentRequest)`  | Create a normal payment transaction.                                                                  |  `paymentRequest: PaymentRequest`  | `PaymentRequestResponse`   |
|  `GET`  |  `getPaymentByTransactionId(@Path("transactionId") transactionId: UUID)`  | Retrieve an existing payment by transaction ID. |  `transactionId: UUID`  |  `Payment`  |
| `GET`  |  `getGroupedPaymentProviders(@Query("amount") amount: Long? = null, @Query("groups") groups: List<Groups>? = null, @Query("language") language: Language? = null,)`  | Retrieve payment group data containing localized names, icon URLs, and grouped providers.              |  `amount: Long?, groups: List<Groups>?, language: Language?`  | `GroupedPaymentProvidersResponse`|

**Token Payment APIs**  
| API Method Name                       | Description                                                               | Parameters                       | Expected Response     |  
|---------------------------------------|---------------------------------------------------------------------------|---------------------------------|-----------------------|  
| `requestTokenForTokenizationId`       | Request a card token for given tokenization id                             | `checkoutTokenizationId: String` | `TokenizationRequestResponse`       |  
| `tokenCitAuthorizationHold`           | Request CIT authorization hold on token                                    | `TokenPaymentRequest`            | `TokenPaymentResponse`|  
| `tokenCitCharge`                      | Request CIT charge on token                                               | `TokenPaymentRequest`            | `TokenPaymentResponse`      |  
| `tokenMitAuthorizationHold`           | Request MIT authorization hold on token                                    | `TokenPaymentRequest`            | `TokenPaymentResponse`|  
| `tokenMitCharge`                      | Request MIT Charge on token                                               | `TokenPaymentRequest`            | `TokenPaymentResponse`      |  
| `tokenCommit`                         | Request committing of previously created authorization hold on token      | `transactionId: UUID, TokenPaymentRequest`| `TokenPaymentResponse` |  
| `tokenRevert`                         | Revert of previously created authorization hold on token                  | `transactionId: UUID`            | `TokenPaymentResponse`     |  
| `payAndAddCard`                       | Create a transaction & pay while adding the payment card simultaneously  | `PaymentRequest`                 | `PayAndAddCardResponse`     |

Please consult our [Detailed API Documentation](https://docs.paytrail.com) for further explanations on API definitions.  You can download OpenAPI 3 specification for the API from  [here](https://docs.paytrail.com/paytrail-api.yaml).


### API Usage Example

If you want to handle API calls manually:

1. Setup the api client
```kotlin
private  val apiClient: PaytrailApiClient by lazy { PaytrailApiClient(merchantAccount = YOUR_MERCHANT_ACCOUNT) }   
```
2. Create the PaymentApi service
```kotlin
// You can replace tha parameter isnide create Service with your desired Paytrail API, like TokenPaymentsApi::class.java
private val api by lazy { apiClient.createService(PaymentsApi::class.java) } 
```

3. Example Call for Creating Payment:
```kotlin
	var response = api.createPayment(paymentRequest = "Your payment request"))
```

## What does the Payment-sdk module contain?

The `payment-sdk` module offers a suite of pre-built views designed to facilitate seamless payment integration into your app. These views are a reflection of some key features provided by the Paytrail API.

### Supported Views:

1. **PaytrailPayment**:
   - *Description*: This view displays available payment providers. Once a user selects a provider, they are directed to the corresponding payment provider's webview.
   - *Parameters*: `MerchantAccount`, `PaymentRequest`, and an optional custom `OkHttpClient` (defaults to our internal client if not provided).
   - *Callback*: `onPaymentStateChanged` returns a `PaytrailPaymentState`.

2. **PayAndAddCard**:
   - *Description*: An integrated view allowing users to tokenize a card and make a payment in a single step, streamlining the payment and card saving process.
   - *Parameters*: `MerchantAccount` and `PaymentRequest`.
   - *Callback*: `onPaymentStateChanged` returns a `PaytrailPaymentState`.

3. **AddCardForm**:
   - *Description*: This view focuses solely on card tokenization without initiating a payment. It provides a form for users to save their payment card information.
   - *Parameters*: `MerchantAccount`, `AddCardRequest` (which defines success and failure callback URLs), and a callback to capture `AddCardResult`.
   - *Callback*: If successful, a `tokenizationId` is provided to save the tokenized card ID. You can refer to the `TokenizedCreditCardsScreen` in the demo project for a practical implementation.

4. **PayWithTokenizationId**:
   - *Description*: This view facilitates payments using a saved card.
   - *Parameters*: `PaymentRequest`, `tokenizationId` (obtained from the selected saved card), `paymentType` (CIT/MIT), `chargeType` (AUTH_HOLD or CHARGE), and `MerchantAccount`.
   - *Callbacks*: `PaytrailPaymentState` and `onTokenAvailable`.

### Saved Card Details and Security

For those curious about how card details are stored:

- **Client-side**: Your client app only retains the token ID, ensuring minimal data exposure.

- **Server-side**: The comprehensive details of the tokenized payment card are stored securely on Paytrail servers. This layered security ensures data integrity and user privacy.

- **Displaying Saved Cards**: In your app, when presenting saved cards, you'll see a masked version of the card number. This representation will resemble: "**** **** **** ${card.partialPan}". Additional data, like card type and card image, are also available, enhancing user familiarity without compromising security.

### Handling Payment States with PaytrailPaymentState Callback

This callback serves as an informant throughout the payment process. It details the different stages, their results, and aids in proactive management and reaction to user actions:

- **State**: Reflects the ongoing phase such as:
  - `LOADING_PAYMENT_PROVIDERS`
  - `SHOW_PAYMENT_PROVIDERS`
  - `PAYMENT_IN_PROGRESS`
  - `PAYMENT_OK`
  - `PAYMENT_FAIL`
  - `PAYMENT_CANCELED`
  - `PAYMENT_ERROR`

- **PaytrailPaymentRedirect**: Contains parameters derived from the checkout redirect link in the webview. Notably, it includes details like `transactionId`, `settlementReference`, and more.

- **TokenPaymentResponse**: This is the response for a successful customer-initiated transaction payment request. It provides the `transactionId` and, if necessary, a `threeDSecureUrl`. This URL is for situations when a merchant needs to redirect a customer for 3DS authentication.

- **PaytrailApiErrorResponse**: Represents error responses when interfacing with the Paytrail API. This is particularly beneficial when using views like `PayWithTokenizationId` or `PayAndAddCard`.

- **Exception**: Captures any exceptions that might emerge during operations, ensuring robust error handling.

### Merchant Account Configuration

The Merchant account information is pivotal across all views. A recommended practice is to formulate a static object that encapsulates merchant account details. This object can then be conveniently passed throughout the app, fostering consistency and ease of access.
```kotlin
val SAMPLE_MERCHANT_ACCOUNT = MerchantAccount(id = 375917, secret = "SAIPPUAKAUPPIAS")
```
---
## Using MSDK

### **Creating Payments**

Making payments with MSDK is easy. There are two approaches:

#### **1. Using PaytrailPayment View**

Utilize the `PaytrailPayment` view from our MSDK for a simplified payment process. From displaying available payment providers to processing the final payment, everything's handled:

```kotlin
PaytrailPayment(    
	modifier = Modifier .fillMaxSize() .padding(horizontal = 24.dp),
	paymentRequest = paymentRequest,
	onPaymentStateChanged = { 
		when (it.state) {
		 // Navigate to the last screen for success, failure, etc. 
			PAYMENT_OK,PAYMENT_FAIL, PAYMENT_ERROR, PAYMENT_CANCELED -> { 	
				navController.navigate("Your result screen")
		} else -> { // Handle other payment progress states. } } }, 
		merchantAccount = SAMPLE_MERCHANT_ACCOUNT) 
```
**Example**: Creating a Static Payment Request

```kotlin
fun cartAsPaymentRequest(): PaymentRequest {    
 val cart = shoppingCartState.value    
return PaymentRequest( // Your payment request details... )} 
```


**For Your personal implementation**

1. **Setup the API Client & Create a Payment Request**:
```kotlin
  private val api by lazy {  
       PaytrailApiClient(merchantAccount = SAMPLE_MERCHANT_ACCOUNT)    
 .createService(PaymentsApi::class.java)   }    
 val createPaymentResponse: LiveData<Response<PaymentRequestResponse>> = liveData { try { emit(api.createPayment(paymentRequest = "Your payment request")) } catch (e: Exception) { Log.i("PaymentViewModel", "Error loading payment providers", e) paymentError.postValue(e) } } 
 ```

2. **Render Payment Providers**:  
   The `createPaymentResponse` provides available payment methods. Payments are categorized (e.g., banks, mobile payments, cards). Display them using each method's name and SVG.

3. **Handle User Selection**:  
   When a user selects a payment method, execute the payment in a webview:
``` kotlin
 val url = selectedPaymentMethod.paymentMethod.provider.url   val postParameters = selectedPaymentMethod.formParameters.joinToFormBodyString().toByteArray()    
 webview.postUrl(url, postParameters)    
  ```

For a comprehensive reference, check our MSDK's `PayWithPaymentMethod` [here](https://github.com/paytrail/paytrail-android-sdk/blob/main/payment-sdk/src/main/java/fi/paytrail/paymentsdk/PayWithPaymentMethod.kt) and `PaymentProviders` [here](https://github.com/paytrail/paytrail-android-sdk/blob/main/payment-sdk/src/main/java/fi/paytrail/paymentsdk/PaymentProviders.kt).

### Card tokenization and add card
1. **Add card form**:  
Using Jetpack Composeenter code here you can easily call this view in your app by calling :
```kotlin
 AddCardForm(        request = AddCardRequest(    
            redirectUrls = Callbacks(    
                success = "https://ecom.example.org/success",    
      cancel = "https://ecom.example.org/cancel",    
      ),    
      ),    
      onAddCardResult = {    
      // Call state.redirectRequest.url if necessary.    
     // // The WebView in SDK for adding card does not follow the final HTTP // redirect to AddCardRequest.redirectUrls.success/cancel URLs. If your // system depends on call to these URLs happening, application needs to // make this call. This can be done either by opening a WebView to the URL, // or using a HTTP client (e.g. OkHttp) to call the URL.    
      coroutineScope.launch {    
      if (it.result == AddCardResult.Result.SUCCESS) {    
                    // Store the tokenization ID securely for later use. The tokenization ID    
     // can be used for retrieving the actual payment token, and masked card // details.    
      tokenizedCardsRepository.saveTokenizationId(it.redirect!!.tokenizationId!!)    
                }    
        
                // Once tokenization result is available, remove the view from    
     // composition / view tree.  navController.navigateUp()    
            }    
     },    
      merchantAccount = SAMPLE_MERCHANT_ACCOUNT,    
    )  
```
**For Your personal implementation**  You will have to create a webview with that load [add card form url](https://services.paytrail.com/tokenization/addcard-form), you will need to pass postParameters that you can get after creating `AddCardFormRequest`.
```kotlin
val request  = AddCardRequest(    
    redirectUrls = Callbacks(    
        success = "https://ecom.example.org/success",    
  cancel = "https://ecom.example.org/cancel",    
  )  
)  
 val addCardFormRequest =    AddCardFormRequest(    
        checkoutAccount = "Your Merchant id ",    
  checkoutMethod = "POST",    
  checkoutAlgorithm = PaytrailHmacCalculator.ALGORITHM_SHA512,    
  checkoutRedirectSuccessUrl = "request.redirectUrls.success,    
  checkoutRedirectCancelUrl = request.redirectUrls.cancel,    
  checkoutCallbackSuccessUrl = request.callbackUrls?.success,    
  checkoutCallbackCancelUrl = request.callbackUrls?.cancel,    
  language = request.language,    
  checkoutNonce = UUID.randomUUID().toString(),    
  checkoutTimestamp = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now()),    
  ).withSignature(account = merchantAccount)  
  ```
Then

``` kotlin
 val url = "https://services.paytrail.com/tokenization/addcard-form"
 val postParameters = addCardFormRequest.asPostParams() 
webview.postUrl(url, postParameters) 
 ```

  2. **PayAndAddCard**:
```kotlin
//You create a payment request  
val paymentRequest = remember { shoppingCartRepository.cartAsPaymentRequest() }
PayAndAddCard(    
    modifier = Modifier.fillMaxSize(),    
	paymentRequest = paymentRequest,    
	onPaymentStateChanged = YOUR_CALL_BACK,    
	merchantAccount = SAMPLE_MERCHANT_ACCOUNT, )  
```
**For custom implementation**
- Create Payment requests as show in previous examples
- Send request this api request
    ```kotlin
  val response = api.payAndAddCard(paymentRequest = paymentRequest)  
    ```
- If the response is successful, get response.body().redirectUrl that you will pass to your webview
- Note the token is **not** included in the redirect URL parameters, as we don't want the user to be able to see the token.

### 3. PayWithTokenizationId

This component allows you to process a charge using a token, which you've previously saved after the user added their card. In line with the European PSD2 directive, electronic payments are classified into two categories: MIT (Merchant Initiated Transactions) and CIT (Customer Initiated Transactions). [Learn more about charging with a token here](https://docs.paytrail.com/#/?id=charging-a-token).

By using the `PayWithTokenizationId` view component, we've made it simple for you to charge your customer based on the charge type and payment type. We also manage the 3D secure authentication process if required.

Example of using `PayWithTokenizationId`:

```kotlin
PayWithTokenizationId(  
    modifier = Modifier.fillMaxSize(),  
    paymentRequest = paymentRequest,  
    tokenizationId = tokenizationId,  
    paymentType = "Either AUTH_HOLD or CHARGE",  
    chargeType = "Either MIT or CIT",  
    onPaymentStateChanged = { state -> handlePaymentState(paymentId, state) },  
    onTokenAvailable = { token -> saveToken(paymentId, token) },  
    merchantAccount = SAMPLE_MERCHANT_ACCOUNT
)
```

**For Custom Implementations**:

- Start by getting the saved card token from your local database.
- Request a usable token for payment with:

```kotlin
val response = api.requestTokenForTokenizationId("Your saved tokenization ID")
```

- Next, set up a token payment request.
- Depending on your needs, call one of these APIs:

```kotlin
// For a CIT charge
api.tokenCitCharge(tokenPaymentRequest)

// For a CIT authorization hold
api.tokenCitAuthorizationHold(tokenPaymentRequest)

// For an MIT charge
api.tokenMitCharge(tokenPaymentRequest)

// For an MIT authorization hold
api.tokenMitAuthorizationHold(tokenPaymentRequest)
```
- Review the response. If `threeDSecureUrl` is present, redirect the customer to this URL for 3D Secure authentication using a WebView.


## References
 **Demo application** | https://github.com/paytrail/paytrail-android-sdk/tree/main/demo-app

 **OpenAPI 3 specification** | https://docs.paytrail.com/paytrail-api.yaml

**Create a normal payment** | https://docs.paytrail.com/#/?id=create

**Payment card tokenization** | https://docs.paytrail.com/#/?id=adding-tokenizing-cards

**Create a token payment** | https://docs.paytrail.com/#/?id=adding-tokenizing-cards

**Pay and add card** | https://docs.paytrail.com/#/?id=pay-and-add-card

**Authentication** | https://docs.paytrail.com/#/?id=authentication

**HAMC signature online calculator** | https://dinochiesa.github.io/hmachash/index.html

**Apple pay** | https://docs.paytrail.com/#/?id=apple-pay