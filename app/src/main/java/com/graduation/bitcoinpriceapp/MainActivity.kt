package com.graduation.bitcoinpriceapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

data class BitcoinPrice(
    val bpi: Bpi
)

data class Bpi(
    val EUR: Currency
)

data class Currency(
    val rate_float: Double
)

val httpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    .build()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            var bitcoinPrice by remember {
                mutableStateOf<BitcoinPrice?>(null)
            }

            LaunchedEffect(Unit) {
                scope.launch(Dispatchers.IO) {
                    try {
                        val request = Request.Builder()
                            .url("https://api.coindesk.com/v1/bpi/currentprice.json").build()

                        val response = httpClient.newCall(request).execute().body!!.string()
                        bitcoinPrice = Gson().fromJson(response, BitcoinPrice::class.java)
                    } catch (e: Exception) {
                        e.message?.let { Log.e("HTTP Request failed", it) }
                    }
                }
            }

            if (bitcoinPrice == null) {
                Text("Loading...")
            } else {
                Text(
                    text = "Bitcoin price in EUR: ${bitcoinPrice!!.bpi.EUR.rate_float}",
                    fontSize = 20.sp
                )
            }
        }
    }
}

