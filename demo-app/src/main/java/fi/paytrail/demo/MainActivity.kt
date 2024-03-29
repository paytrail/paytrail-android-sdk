package fi.paytrail.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import fi.paytrail.demo.payments.PaymentConfirmation
import fi.paytrail.demo.payments.PaymentDetails
import fi.paytrail.demo.payments.PaymentListing
import fi.paytrail.demo.payments.PaymentRepository
import fi.paytrail.demo.shoppingcart.CustomerDetailScreen
import fi.paytrail.demo.shoppingcart.ShoppingCartRepository
import fi.paytrail.demo.shoppingcart.ShoppingCartScreen
import fi.paytrail.demo.shoppingcart.ShoppingCartViewModel
import fi.paytrail.demo.shoppingcart.currencyFormatter
import fi.paytrail.demo.tokenization.TokenizedCardsRepository
import fi.paytrail.demo.tokenization.TokenizedCreditCard
import fi.paytrail.demo.tokenization.TokenizedCreditCardListing
import fi.paytrail.demo.tokenization.TokenizedCreditCardsScreen
import fi.paytrail.demo.tokenization.TokenizedCreditCardsViewModel
import fi.paytrail.demo.ui.theme.MyColors.LightGrey
import fi.paytrail.demo.ui.theme.PaytrailDemoTheme
import fi.paytrail.paymentsdk.PayAndAddCard
import fi.paytrail.paymentsdk.PaymentStateChangeListener
import fi.paytrail.paymentsdk.PaytrailPayment
import fi.paytrail.paymentsdk.RequestStatus
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_CANCELED
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_ERROR
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_FAIL
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_OK
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.SHOW_PAYMENT_PROVIDERS
import fi.paytrail.paymentsdk.tokenization.AddCardForm
import fi.paytrail.paymentsdk.tokenization.AddCardStatusChangedListener
import fi.paytrail.paymentsdk.tokenization.PayWithTokenizationId
import fi.paytrail.paymentsdk.tokenization.TokenPaymentChargeType
import fi.paytrail.paymentsdk.tokenization.TokenPaymentType
import fi.paytrail.paymentsdk.tokenization.model.AddCardRequest
import fi.paytrail.paymentsdk.tokenization.model.AddCardResult
import fi.paytrail.sdk.apiclient.models.Callbacks
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

/* Locally assigned ID of a payment. Note that this is not same as the transaction ID. */
const val NAV_ARG_PAYMENT_ID = "paymentId"
private const val NAV_ARG_TOKENIZATION_ID = "tokenizationId"
private const val NAV_ARG_PAYMENT_TYPE = "paymentType"
private const val NAV_ARG_CHARGE_TYPE = "chargeType"

private const val NAV_SHOPPING_CART = "shopping_cart"
private const val NAV_CREATE_PAYMENT = "payment/create"
private const val NAV_CARDS = "cards"
private const val NAV_ADD_CARD = "cards/tokenize"
private const val NAV_PAY_AND_ADD_CARD = "payment/pay_and_add_card"
private const val NAV_PAY_WITH_TOKENIZATION_ID =
    "cards/{$NAV_ARG_TOKENIZATION_ID}/{$NAV_ARG_PAYMENT_TYPE}/{$NAV_ARG_CHARGE_TYPE}"
private const val NAV_PAYMENT_LISTING = "payments"
private const val NAV_PAYMENT_DETAILS = "payment/{$NAV_ARG_PAYMENT_ID}"
private const val NAV_CUSTOMER_DETAIL = "customer_detail"
private const val NAV_PAYMENT_CONFIRMATION = "payment_confirmation"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var shoppingCartRepository: ShoppingCartRepository

    @Inject
    lateinit var tokenizedCardsRepository: TokenizedCardsRepository

    @Inject
    lateinit var paymentRepository: PaymentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var paymentState: Pair<UUID, PaytrailPaymentState>? by mutableStateOf(null)

        setContent {
            // For demo purpose, we forced light theme
            PaytrailDemoTheme(dynamicColor = false, darkTheme = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()
                        PaytrailDemoAppTopBar()

                        MainContent(
                            modifier = Modifier.weight(1f),
                            navController = navController,
                            paymentState = paymentState?.second,
                            onPaymentStateChanged = { transactionId: UUID, state: PaytrailPaymentState ->
                                paymentState = transactionId to state
                                paymentRepository.store(transactionId, state)
                                when (state.state) {
                                    // When handling payment results, application should navigate
                                    // out of payment when a success or error state has been
                                    // reached.
                                    PAYMENT_OK, PAYMENT_FAIL, PAYMENT_ERROR, PAYMENT_CANCELED -> {
                                        navController.navigate(NAV_PAYMENT_CONFIRMATION) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                inclusive = true
                                            }
                                        }
                                    }

                                    else -> {
                                        // Payment progress state can be tracked here.
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PaytrailDemoAppTopBar() {
        TopAppBar(
            modifier = Modifier
                .shadow(4.dp)
                .zIndex(1f),
            colors = TopAppBarDefaults.topAppBarColors(),
            title = { /* no title content */ },
            navigationIcon = {
                Image(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    painter = painterResource(id = fi.paytrail.paymentsdk.R.drawable.paytrail_logo),
                    contentDescription = null,
                )
            },
        )
    }

    private fun shouldShowStatus(paymentResult: PaytrailPaymentState?): Boolean =
        paymentResult?.state in setOf(PAYMENT_OK, PAYMENT_FAIL, PAYMENT_ERROR)

    @Composable
    private fun MainContent(
        modifier: Modifier = Modifier,
        navController: NavHostController,
        paymentState: PaytrailPaymentState?,
        onPaymentStateChanged: (UUID, PaytrailPaymentState) -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()

        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = NAV_SHOPPING_CART,
        ) {
            composable(NAV_SHOPPING_CART) {
                ShoppingCartScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightGrey),
                    viewModel = hiltViewModel(),
                    payAction = { navController.navigate(NAV_CREATE_PAYMENT) },
                    payAndAddCardAction = { navController.navigate(NAV_PAY_AND_ADD_CARD) },
                    cardsAction = { navController.navigate(NAV_CARDS) },
                    showPaymentHistory = { navController.navigate(NAV_PAYMENT_LISTING) },
                    continueAction = { navController.navigate(NAV_CUSTOMER_DETAIL) },
                )
            }
            composable(NAV_CUSTOMER_DETAIL) {
                CustomerDetailScreen(
                    cancelAction = {
                        navController.popBackStack()
                    },
                    toPayAction = {
                        navController.navigate(NAV_CREATE_PAYMENT)
                    },
                )
            }
            composable(NAV_CARDS) {
                TokenizedCreditCardsScreen(
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
                val paymentRequest = remember { shoppingCartRepository.cartAsPaymentRequest() }
                val paymentId = remember { UUID.randomUUID() }
                val args = it.arguments!!
                val tokenizationId = args.getString(NAV_ARG_TOKENIZATION_ID)!!
                val chargeType =
                    TokenPaymentChargeType.valueOf(args.getString(NAV_ARG_CHARGE_TYPE)!!)
                val paymentType =
                    TokenPaymentType.valueOf(args.getString(NAV_ARG_PAYMENT_TYPE)!!)
                LaunchedEffect(paymentId, paymentRequest) {
                    paymentRepository.store(paymentId, paymentRequest)
                }
                PayWithTokenizationId(
                    modifier = Modifier.fillMaxSize(),
                    paymentRequest = paymentRequest,
                    tokenizationId = tokenizationId,
                    paymentType = paymentType,
                    chargeType = chargeType,
                    onPaymentStateChanged = object : PaymentStateChangeListener {
                        override fun onPaymentStateChanged(state: PaytrailPaymentState) {
                            onPaymentStateChanged(paymentId, state)
                        }
                    },
                    onTokenAvailable = { token -> paymentRepository.storeToken(paymentId, token) },
                    merchantAccount = SAMPLE_MERCHANT_ACCOUNT,
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
                    onAddCardResult = object : AddCardStatusChangedListener {
                        override fun onAddCardResult(addCardResult: AddCardResult) {
                            // Call state.redirectRequest.url if necessary.
                            //
                            // The WebView in SDK for adding card does not follow the final HTTP
                            // redirect to AddCardRequest.redirectUrls.success/cancel URLs. If your
                            // system depends on call to these URLs happening, application needs to
                            // make this call. This can be done either by opening a WebView to the URL,
                            // or using a HTTP client (e.g. OkHttp) to call the URL.

                            coroutineScope.launch {
                                if (addCardResult.result == AddCardResult.Result.SUCCESS) {
                                    // Store the tokenization ID securely for later use. The tokenization ID
                                    // can be used for retrieving the actual payment token, and masked card
                                    // details.

                                    tokenizedCardsRepository.saveTokenizationId(addCardResult.redirect!!.tokenizationId!!)
                                }

                                // Once tokenization result is available, remove the view from
                                // composition / view tree.
                                navController.navigateUp()
                            }
                        }
                    },
                    merchantAccount = SAMPLE_MERCHANT_ACCOUNT,
                )
            }

            composable(NAV_CREATE_PAYMENT) {
                val cardsViewModel: TokenizedCreditCardsViewModel = hiltViewModel()
                val paymentId = remember { UUID.randomUUID() }
                val paymentRequest = remember { shoppingCartRepository.cartAsPaymentRequest() }
                LaunchedEffect(paymentId, paymentRequest) {
                    paymentRepository.store(paymentId, paymentRequest)
                }
                PaymentScreen(
                    paymentRequest = paymentRequest,
                    paymentState = paymentState,
                    onPaymentStateChanged = object : PaymentStateChangeListener {
                        override fun onPaymentStateChanged(state: PaytrailPaymentState) {
                            onPaymentStateChanged(paymentId, state)
                        }
                    },
                    navController = navController,
                    cards = cardsViewModel.cards.collectAsState(initial = emptyList()).value,
                )
            }
            // TODO this may come in the v2 of this demo
            composable(NAV_PAY_AND_ADD_CARD) {
                val paymentId = remember { UUID.randomUUID() }
                val paymentRequest = remember { shoppingCartRepository.cartAsPaymentRequest() }
                LaunchedEffect(paymentId, paymentRequest) {
                    paymentRepository.store(paymentId, paymentRequest)
                }
                PayAndAddCard(
                    modifier = Modifier.fillMaxSize(),
                    paymentRequest = paymentRequest,
                    onPaymentStateChanged = object : PaymentStateChangeListener {
                        override fun onPaymentStateChanged(state: PaytrailPaymentState) {
                            onPaymentStateChanged(paymentId, state)
                        }
                    },
                    merchantAccount = SAMPLE_MERCHANT_ACCOUNT,
                )
            }

            composable(NAV_PAYMENT_LISTING) {
                PaymentListing(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = hiltViewModel(),
                    showPaymentDetails = {
                        navController.navigate(
                            NAV_PAYMENT_DETAILS.replace("{$NAV_ARG_PAYMENT_ID}", it.toString()),
                        )
                    },
                )
            }

            composable(
                NAV_PAYMENT_DETAILS,
                arguments = listOf(
                    navArgument(NAV_ARG_PAYMENT_ID) { type = NavType.StringType },
                ),
            ) {
                PaymentDetails(modifier = Modifier.fillMaxSize(), viewModel = hiltViewModel())
            }

            composable(
                NAV_PAYMENT_CONFIRMATION,
            ) {
                PaymentConfirmation(paymentState = paymentState) {
                    navController.navigate(NAV_SHOPPING_CART)
                }
            }
        }
    }

    @Composable
    private fun PaymentScreen(
        paymentRequest: PaymentRequest,
        paymentState: PaytrailPaymentState?,
        onPaymentStateChanged: PaymentStateChangeListener,
        navController: NavController,
        cards: List<Pair<String, Flow<RequestStatus<TokenizedCreditCard>>>>,
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            if (paymentState?.state in setOf(
                    PaytrailPaymentState.State.LOADING_PAYMENT_PROVIDERS,
                    PaytrailPaymentState.State.SHOW_PAYMENT_PROVIDERS,
                )
            ) {
                ShoppingCartSummaryHeader(hiltViewModel())
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = LightGrey)
                    .padding(top = 16.dp),
            ) {
                if (paymentState?.state == SHOW_PAYMENT_PROVIDERS) {
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = "Saved cards",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    TokenizedCreditCardListing(
                        modifier = Modifier.fillMaxSize(),
                        cards = cards,
                        onCardClick = { tokenizationId, _ ->
                            navController.navigate(
                                NAV_PAY_WITH_TOKENIZATION_ID
                                    .replace("{$NAV_ARG_TOKENIZATION_ID}", tokenizationId)
                                    .replace("{$NAV_ARG_PAYMENT_TYPE}", TokenPaymentType.CIT.name)
                                    .replace(
                                        "{$NAV_ARG_CHARGE_TYPE}",
                                        TokenPaymentChargeType.CHARGE.name,
                                    ),
                            )
                        },
                    )

                    Button(
                        modifier = Modifier
                            .widthIn(min = 80.dp)
                            .padding(top = 8.dp, bottom = 16.dp)
                            .align(CenterHorizontally),
                        onClick = { navController.navigate(NAV_ADD_CARD) },
                    ) {
                        Text(stringResource(R.string.manage_cards_button_add_card))
                    }

                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = stringResource(R.string.choose_payment_provider),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                PaytrailPayment(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    paymentRequest = paymentRequest,
                    onPaymentStateChanged = onPaymentStateChanged,
                    merchantAccount = SAMPLE_MERCHANT_ACCOUNT,
                )
            }
        }
    }

    @Composable
    private fun ShoppingCartSummaryHeader(viewmodel: ShoppingCartViewModel) {
        val totalCartPrice =
            viewmodel.totalAmount.collectAsState(initial = BigDecimal.ZERO).value
        val items = viewmodel.items.collectAsState(initial = emptyList()).value
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Text(
                text = "Cart summary",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(26.dp))

            items.forEach {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${it.amount} x ${it.name}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${currencyFormatter.format(it.totalPrice)} €")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleMedium) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Total price",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${currencyFormatter.format(totalCartPrice)} €")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}
