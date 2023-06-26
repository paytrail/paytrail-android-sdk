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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import fi.paytrail.demo.repository.ShoppingCartRepository
import fi.paytrail.demo.ui.theme.PaytrailSDKTheme
import fi.paytrail.paymentsdk.PaytrailPayment
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_CANCELED
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_ERROR
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_FAIL
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_OK
import fi.paytrail.sdk.apiclient.MerchantAccount
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val merchantAccount: MerchantAccount = MerchantAccount(
        375917,
        "SAIPPUAKAUPPIAS",
    )

    @Inject
    lateinit var shoppingCartRepository: ShoppingCartRepository

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
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = "shopping_cart",
        ) {
            composable("shopping_cart") {
                ShoppingCart(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = hiltViewModel(),
                ) { navController.navigate("payment") }
            }
            composable("payment") {
                // TODO: Pass as parameters:
                //    * items
                //    * shop-in-shop stuff?
                //    * theming? (could use MyFancyTheme {} wrapper as well...)
                val paymentRequest = remember { shoppingCartRepository.cartAsPaymentRequest() }
                PaytrailPayment(
                    modifier = Modifier.fillMaxSize(),
                    payment = paymentRequest,
                    merchant = merchantAccount,
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
                                    route = "shopping_cart",
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
