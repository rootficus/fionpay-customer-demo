package com.rootficus.demofion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rootficus.demofion.ui.theme.DemoFionTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    lateinit var showBottomSheet: MutableState<Boolean>
    lateinit var isLoading: MutableState<Boolean>
    lateinit var bankType: MutableState<String>
    lateinit var amount: MutableState<String>
    lateinit var customerNumber: MutableState<String>
    lateinit var sheetHead: MutableState<String>
    lateinit var responseData: MutableState<String>
    lateinit var isCustomerNumberVisible: MutableState<Boolean>
    lateinit var isVisible: MutableState<Boolean>

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Set log level
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor()) // Add authentication interceptor
        .addInterceptor(loggingInterceptor)
        .build()

    // Retrofit instance
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://testapi.fionpay.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val apiService: MyApiService = retrofit.create(MyApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showBottomSheet = mutableStateOf(false)
        isLoading = mutableStateOf(false)
        bankType = mutableStateOf("")
        amount = mutableStateOf("")
        customerNumber = mutableStateOf("")
        sheetHead = mutableStateOf("")
        responseData = mutableStateOf("")
        isVisible = mutableStateOf(false)
        isCustomerNumberVisible = mutableStateOf(false)
        setContent {
            MyComposable(
                apiService,
                showBottomSheet,
                isLoading,
                bankType,
                amount,
                customerNumber,
                isCustomerNumberVisible,
                sheetHead,
                isVisible,
                responseData
            )
        }
    }
}

// Interceptor for adding authentication headers
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json") // Add Content-Type header
            .addHeader(
                "X-Authorization-Aceess",
                "d5b338ab2fef5ddf59fb76c9d4db92e0fe5865135a8d4510c092e276e732d26b"
            ) // Add Authorization header
            .addHeader(
                "X-Authorization-Public",
                "3bb6ba17764028ffbe7fa3067ca4cca237b522073164c8217f45c3dcba1453bf"
            ) // Add Authorization header
            .build()
        return chain.proceed(request)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyComposable(
    apiService: MyApiService,
    showBottomSheet: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
    bankType: MutableState<String>,
    amount: MutableState<String>,
    customerNumber: MutableState<String>,
    isCustomerNumberVisible: MutableState<Boolean>,
    sheetHead: MutableState<String>,
    isVisible: MutableState<Boolean>,
    responseData: MutableState<String>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp / 2

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(topBar = {
            TopAppBar(
                title = { Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                    Text(text = "Exmple 1x", fontSize = 20.sp)
                } },
                modifier = Modifier.height(50.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(innerPadding)
            ) {

                showCard(
                    screenHeight,
                    showBottomSheet,
                    isCustomerNumberVisible,
                    bankType,
                    sheetHead,
                    "Deposit",
                    Arrangement.Center
                )
                showCard(
                    screenHeight,
                    showBottomSheet,
                    isCustomerNumberVisible,
                    bankType,
                    sheetHead,
                    "Withdraw",
                    Arrangement.Top
                )

                if (showBottomSheet.value) {
                    BottomSheet(
                        showBottomSheet,
                        sheetHead,
                        sheetState,
                        amount,
                        bankType,
                        customerNumber,
                        isCustomerNumberVisible,
                        scope,
                        isLoading,
                        context,
                        apiService,
                        isVisible,
                        responseData
                    )
                }
            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    showBottomSheet: MutableState<Boolean>,
    sheetHead: MutableState<String>,
    sheetState: SheetState,
    amount: MutableState<String>,
    bankType: MutableState<String>,
    customerNumber: MutableState<String>,
    isCustomerNumberVisible: MutableState<Boolean>,
    scope: CoroutineScope,
    isLoading: MutableState<Boolean>,
    context: Context,
    apiService: MyApiService,
    isVisible: MutableState<Boolean>,
    responseData: MutableState<String>
) {
    ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet.value = false
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.height(350.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        {
            Text(
                text = sheetHead.value,
                fontSize = 22.sp,
                fontFamily = FontFamily.SansSerif,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.size(20.dp))
            OutlinedTextField(
                value = amount.value,
                onValueChange = {
                    amount.value = it
                    responseData.value = ""
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Amount") }
            )
            if (isCustomerNumberVisible.value) {
                OutlinedTextField(
                    value = customerNumber.value,
                    onValueChange = {
                        customerNumber.value = it
                        responseData.value = ""
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Customer Number") }
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.green)),
                onClick = {
                    isLoading.value = true
                    isVisible.value = true
                    // Call the API when the button is clicked
                    if (!isCustomerNumberVisible.value && amount.value.isNotEmpty()) {
                        callApi(
                            "Deposit",
                            scope,
                            apiService,
                            context,
                            showBottomSheet,
                            isLoading,
                            bankType,
                            amount, customerNumber,
                            responseData
                        )

                    } else if (isCustomerNumberVisible.value && amount.value.isNotEmpty() && customerNumber.value.isNotEmpty()) {
                        callApi(
                            "Withdraw",
                            scope,
                            apiService,
                            context,
                            showBottomSheet,
                            isLoading,
                            bankType,
                            amount,
                            customerNumber,
                            responseData
                        )
                    } else {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            "Please enter value",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                enabled = !isLoading.value
            ) {
                if (isLoading.value) {
                    // Show loading indicator when API request is in progress
                    CircularProgressIndicator()
                } else {
                    // Show button text
                    Text("Submit")
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            // Sheet content
            /*   Button(
                   shape = RoundedCornerShape(5.dp),
                   onClick = {
                       scope.launch { sheetState.hide() }.invokeOnCompletion {
                           if (!sheetState.isVisible) {
                               showBottomSheet.value = false
                           }
                       }
                   },
                   colors = ButtonDefaults.buttonColors(colorResource(id = R.color.red))
               ) {
                   Text("Cancel")
               }*/
            if (responseData.value.isNotEmpty()) {
                Text(
                    "${responseData.value}",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 16.sp,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.size(20.dp))
            }

        }

    }
}

@Composable
fun showCard(
    screenHeight: Dp,
    showBottomSheet: MutableState<Boolean>,
    isCustomerNumberVisible: MutableState<Boolean>,
    bankType: MutableState<String>,
    sheetHead: MutableState<String>,
    type: String,
    top: Arrangement.Vertical
) {
    val numberVisible: Boolean = type != "Deposit"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            verticalArrangement = top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = type,
                modifier = Modifier.padding(20.dp),
                fontSize = 22.sp,
                fontFamily = FontFamily.SansSerif,
                fontStyle = FontStyle.Italic
            )
            Row {
                Button(
                    onClick = {
                        showBottomSheet.value = true
                        isCustomerNumberVisible.value = numberVisible
                        sheetHead.value = "$type: Fionpay Nagad"
                        bankType.value = "Nagad"
                    },
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text = "Fionpay Nagad", fontFamily = FontFamily.SansSerif)
                }
                Spacer(modifier = Modifier.size(20.dp))
                Button(
                    onClick = {
                        showBottomSheet.value = true
                        isCustomerNumberVisible.value = numberVisible
                        sheetHead.value = "$type: Fionpay Bkash"
                        bankType.value = "Bkash"
                    },
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text = "Fionpay Bkash", fontFamily = FontFamily.SansSerif)
                }
            }
        }
    }
}

fun callApi(
    type: String,
    scope: CoroutineScope,
    apiService: MyApiService,
    context: Context,
    showBottomSheet: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
    bankType: MutableState<String>,
    amount: MutableState<String>,
    customerNumber: MutableState<String>,
    responseError: MutableState<String>
) {
    isLoading.value = true
    val randomNumber = generateRandomNumber(10)
    var responseData = ""
    scope.launch {
        try {
            // Perform API request
            val response = apiService.fetchData(
                YourRequestBody(
                    amount = amount.value.toLong(),
                    bank_type = bankType.value,
                    request_type = type,
                    redirect_url = "https://1x-sage.vercel.app",
                    merchant_payment_id = randomNumber.toString(),
                    cust_phone = customerNumber.value
                )
            )
            isLoading.value = false
            if (response.status == 200) {
                showBottomSheet.value = false
                // Update UI with response data
                responseData = response.redirect_url.toString()
                amount.value = ""
                customerNumber.value = ""
                CallActivity(responseData = responseData, current = context)
            } else {
                responseError.value = response.msg.toString()
            }


        } catch (e: Exception) {
            // Handle API request failure
            amount.value = ""
            customerNumber.value = ""
            responseError.value = "Error fetching data: ${e.message}"
        } finally {
            isLoading.value = false
        }
    }
}

fun CallActivity(responseData: String, current: Context) {
    val intent = Intent(current, WebClass::class.java)
    intent.putExtra("url", responseData.toString())
    current.startActivity(intent)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DemoFionTheme {
        Greeting("Android")
    }
}


fun generateRandomNumber(digits: Int): Long {
    require(digits > 0) { "Number of digits must be greater than 0" }
    val min = Math.pow(10.0, (digits - 1).toDouble()).toLong()
    val max = Math.pow(10.0, digits.toDouble()).toLong() - 1
    return Random.nextLong(min, max)
}
