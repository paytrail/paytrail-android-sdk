package fi.paytrail.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import fi.paytrail.demo.repository.ShoppingCartRepository
import fi.paytrail.demo.tokenization.ManageCards
import fi.paytrail.demo.tokenization.SavedCardsRepository
import fi.paytrail.demo.ui.theme.PaytrailSDKTheme
import fi.paytrail.paymentsdk.PaytrailPayment
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_CANCELED
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_ERROR
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_FAIL
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_OK
import fi.paytrail.paymentsdk.tokenization.AddCardForm
import fi.paytrail.paymentsdk.tokenization.model.AddCardRequest
import fi.paytrail.paymentsdk.tokenization.model.AddCardResult
import fi.paytrail.sdk.apiclient.models.Callbacks
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NAV_SHOPPING_CART = "shopping_cart"
private const val NAV_PAYMENT = "payment"
private const val NAV_MANAGE_CARDS = "manage_cards"
private const val NAV_ADD_CARD = "tokenize_card"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var shoppingCartRepository: ShoppingCartRepository

    @Inject
    lateinit var savedCardsRepository: SavedCardsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var paymentState: PaytrailPaymentState? by mutableStateOf(null)

        setContent {
            PaytrailSDKTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AnimatedVisibility(visible = shouldShowStatus(paymentState)) {
                            PaymentResultView(
                                paymentResult = paymentState,
                                onHide = { paymentState = null },
                            )
                        }

                        val navController = rememberNavController()
                        ApplicationContent(
                            modifier = Modifier.weight(1f),
                            navController = navController,
                            onPaymentStateChanged = { paymentState = it },
                        )
                    }
                }
            }
        }
    }

    private fun shouldShowStatus(paymentResult: PaytrailPaymentState?): Boolean =
        paymentResult?.state in setOf(PAYMENT_OK, PAYMENT_FAIL, PAYMENT_ERROR)

    @Composable
    private fun ApplicationContent(
        modifier: Modifier = Modifier,
        navController: NavHostController,
        onPaymentStateChanged: (PaytrailPaymentState) -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()

        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = NAV_SHOPPING_CART,
        ) {
            composable(NAV_SHOPPING_CART) {
                ShoppingCart(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = hiltViewModel(),
                    payAction = { navController.navigate(NAV_PAYMENT) },
                ) { navController.navigate(NAV_MANAGE_CARDS) }
            }

            composable(NAV_MANAGE_CARDS) {
                ManageCards(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = hiltViewModel(),
                    addCardAction = { navController.navigate(NAV_ADD_CARD) },
                )
            }

            composable(NAV_ADD_CARD) {
                val context = LocalContext.current
                AddCardForm(
                    request = AddCardRequest(
                        redirectUrls = Callbacks(
                            success = "https://ecom.example.org/success",
                            cancel = "https://ecom.example.org/cancel",
                        ),
                    ),
                    onAddCardResult = {
                        // Call state.redirectRequest.url if necessary.
                        //
                        // The WebView in SDK for adding card does not follow the final HTTP
                        // redirect to AddCardRequest.redirectUrls.success/cancel URLs. If your
                        // system depends on call to these URLs happening, application needs to
                        // make this call. This can be done either by opening a WebView to the URL,
                        // or using a HTTP client (e.g. OkHttp) to call the URL.

                        coroutineScope.launch {
                            if (it.result == AddCardResult.Result.SUCCESS) {
                                // Store the tokenization ID securely for later use. The tokenization ID
                                // can be used for retrieving the actual payment token, and masked card
                                // details.

                                savedCardsRepository.saveTokenizationId(it.redirect!!.tokenizationId!!)
                            }

                            // Once tokenization result is available, remove the view from
                            // composition / view tree.
                            navController.navigateUp()
                        }
                    },
                )
            }

            composable(NAV_PAYMENT) {
                // TODO: Pass as parameters:
                //    * items
                //    * shop-in-shop stuff?
                //    * theming? (could use MyFancyTheme {} wrapper as well...)
                val paymentRequest = remember { shoppingCartRepository.cartAsPaymentRequest() }
                PaytrailPayment(
                    modifier = Modifier.fillMaxSize(),
                    payment = paymentRequest,
                    onPaymentStateChanged = { state ->
                        // TODO: Mark shopping cart as "paid" when called with PAYMENT_OK

                        // Call state.redirectRequest.url if necessary.
                        //
                        // The WebView in SDK for the payment flow does not follow the final HTTP
                        // redirect to PaymentRequest.redirectUrls.success/cancel URLs. If your
                        // system depends on call to these URLs happening, application needs to
                        // make this call. This can be done either by opening a WebView to the URL,
                        // or using a HTTP client (e.g. OkHttp) to call the URL.

                        when (state.state) {
                            PAYMENT_OK, PAYMENT_FAIL, PAYMENT_ERROR, PAYMENT_CANCELED -> {
                                navController.popBackStack(
                                    route = NAV_SHOPPING_CART,
                                    inclusive = false,
                                )
                                onPaymentStateChanged(state)
                            }

                            else -> {
                                // navigate out of payment only when result is ok or fail
                            }
                        }
                    },
                )
            }
        }
    }
}
