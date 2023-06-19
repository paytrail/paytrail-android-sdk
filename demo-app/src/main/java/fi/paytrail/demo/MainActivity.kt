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
import fi.paytrail.paymentsdk.PaytrailPaymentResult
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

        var paymentResult: PaytrailPaymentResult? by mutableStateOf(null)

        setContent {
            PaytrailSDKTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AnimatedVisibility(visible = shouldShowStatus(paymentResult)) {
                            PaymentResultView(
                                paymentResult = paymentResult,
                                onHide = { paymentResult = null },
                            )
                        }

                        val navController = rememberNavController()
                        ApplicationContent(
                            modifier = Modifier.weight(1f),
                            navController = navController,
                            onPaymentResult = { paymentResult = it },
                        )
                    }
                }
            }
        }
    }

    private fun shouldShowStatus(paymentResult: PaytrailPaymentResult?): Boolean {
        return paymentResult?.status in setOf(
            PaytrailPaymentResult.Status.Ok,
            PaytrailPaymentResult.Status.Fail,
        )
    }

    @Composable
    private fun ApplicationContent(
        modifier: Modifier = Modifier,
        navController: NavHostController,
        onPaymentResult: (PaytrailPaymentResult) -> Unit,
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
                //    * merchant account info?
                //    * shop-in-shop stuff?
                //    * theming? (could use MyFancyTheme {} wrapper as well...)
                val paymentOrder = remember { shoppingCartRepository.cartAsPaymentOrder() }
                PaytrailPayment(
                    modifier = Modifier.fillMaxSize(),
                    payment = paymentOrder,
                    merchant = merchantAccount,
                    onPaymentResult = {
                        when (it.status) {
                            PaytrailPaymentResult.Status.Ok, PaytrailPaymentResult.Status.Fail -> {
                                navController.popBackStack(
                                    route = "shopping_cart",
                                    inclusive = false,
                                )
                                onPaymentResult(it)
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

