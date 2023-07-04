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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import fi.paytrail.demo.repository.ShoppingCartRepository
import fi.paytrail.demo.tokenization.SavedCardsRepository
import fi.paytrail.demo.tokenization.TokenizedCreditCards
import fi.paytrail.demo.ui.theme.PaytrailSDKTheme
import fi.paytrail.paymentsdk.PaytrailPayment
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_CANCELED
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_ERROR
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_FAIL
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_OK
import fi.paytrail.paymentsdk.tokenization.AddCardForm
import fi.paytrail.paymentsdk.tokenization.PayWithTokenizationId
import fi.paytrail.paymentsdk.tokenization.TokenPaymentChargeType
import fi.paytrail.paymentsdk.tokenization.TokenPaymentType
import fi.paytrail.paymentsdk.tokenization.model.AddCardRequest
import fi.paytrail.paymentsdk.tokenization.model.AddCardResult
import fi.paytrail.sdk.apiclient.models.Callbacks
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NAV_ARG_TOKENIZATION_ID = "tokenizationId"
private const val NAV_ARG_PAYMENT_TYPE = "paymentType"
private const val NAV_ARG_CHARGE_TYPE = "chargeType"

private const val NAV_SHOPPING_CART = "shopping_cart"
private const val NAV_PAYMENT = "payment"
private const val NAV_CARDS = "cards"
private const val NAV_ADD_CARD = "cards/tokenize"
private const val NAV_PAY_WITH_TOKENIZATION_ID =
    "cards/{$NAV_ARG_TOKENIZATION_ID}/{$NAV_ARG_PAYMENT_TYPE}/{$NAV_ARG_CHARGE_TYPE}"

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
                        MainContent(
                            modifier = Modifier.weight(1f),
                            navController = navController,
                            onPaymentStateChanged = { state ->
                                paymentState = state
                                when (state.state) {
                                    // When handling payment results, application should navigate
                                    // out of payment when a success or error state has been
                                    // reached.
                                    PAYMENT_OK, PAYMENT_FAIL, PAYMENT_ERROR, PAYMENT_CANCELED -> {
                                        navController.popBackStack(
                                            route = NAV_SHOPPING_CART,
                                            inclusive = false,
                                        )
                                    }

                                    else -> {
                                        // Payment progress state can be tracked here.
                                        // If you want transaction ID,
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    private fun shouldShowStatus(paymentResult: PaytrailPaymentState?): Boolean =
        paymentResult?.state in setOf(PAYMENT_OK, PAYMENT_FAIL, PAYMENT_ERROR)

    @Composable
    private fun MainContent(
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
                ) { navController.navigate(NAV_CARDS) }
            }

            composable(NAV_CARDS) {
                TokenizedCreditCards(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = hiltViewModel(),
                    payWithCardAction = { tokenizationId: String, paymentType: TokenPaymentType, chargeType: TokenPaymentChargeType ->
                        navController.navigate(
                            NAV_PAY_WITH_TOKENIZATION_ID
                                .replace("{$NAV_ARG_TOKENIZATION_ID}", tokenizationId)
                                .replace("{$NAV_ARG_PAYMENT_TYPE}", paymentType.name)
                                .replace("{$NAV_ARG_CHARGE_TYPE}", chargeType.name),
                        )
                    },
                    addCardAction = { navController.navigate(NAV_ADD_CARD) },
                )
            }

            composable(
                route = NAV_PAY_WITH_TOKENIZATION_ID,
                arguments = listOf(
                    navArgument(NAV_ARG_TOKENIZATION_ID) { type = NavType.StringType },
                    navArgument(NAV_ARG_PAYMENT_TYPE) { type = NavType.StringType },
                    navArgument(NAV_ARG_CHARGE_TYPE) { type = NavType.StringType },
                ),
            ) {
                val args = it.arguments!!
                val tokenizationId = args.getString(NAV_ARG_TOKENIZATION_ID)!!
                val chargeType =
                    TokenPaymentChargeType.valueOf(args.getString(NAV_ARG_CHARGE_TYPE)!!)
                val paymentType =
                    TokenPaymentType.valueOf(args.getString(NAV_ARG_PAYMENT_TYPE)!!)
                PayWithTokenizationId(
                    modifier = Modifier.fillMaxSize(),
                    paymentRequest = shoppingCartRepository.cartAsPaymentRequest(),
                    tokenizationId = tokenizationId,
                    onPaymentStateChanged = onPaymentStateChanged,
                    paymentType = paymentType,
                    chargeType = chargeType,
                )
            }

            composable(NAV_ADD_CARD) {
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
                val paymentRequest =
                    remember { shoppingCartRepository.cartAsPaymentRequest() }
                PaytrailPayment(
                    modifier = Modifier.fillMaxSize(),
                    paymentRequest = paymentRequest,
                    onPaymentStateChanged = onPaymentStateChanged,
                )
            }
        }
    }
}
