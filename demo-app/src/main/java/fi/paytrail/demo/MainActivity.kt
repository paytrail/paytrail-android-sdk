package fi.paytrail.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import fi.paytrail.demo.repository.ShoppingCartRepository
import fi.paytrail.demo.ui.theme.PaytrailSDKTheme
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.paymentsdk.PaytrailPayment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val merchantAccount: MerchantAccount = MerchantAccount(
        375917,
        "SAIPPUAKAUPPIAS"
    )

    @Inject
    lateinit var shoppingCartRepository: ShoppingCartRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaytrailSDKTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        startDestination = "shopping_cart",
                    ) {
                        composable("shopping_cart") {
                            ShoppingCart(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = hiltViewModel()
                            ) { navController.navigate("payment") }
                        }
                        composable("payment") {
                            // TODO: Pass as parameters:
                            //    * items
                            //    * merchant info
                            //    * shop-in-shop stuff?
                            //    * theming? (could use MyFancyTheme {} wrapper as well...)
                            val paymentOrder =
                                remember { shoppingCartRepository.cartAsPaymentOrder() }
                            PaytrailPayment(
                                modifier = Modifier.fillMaxSize(),
                                payment = paymentOrder,
                                merchant = merchantAccount,
                            )
                        }
                    }
                }
            }
        }
    }
}
