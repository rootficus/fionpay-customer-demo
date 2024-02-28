package com.rootficus.demofion

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

class WebClass: ComponentActivity() {
    lateinit var data: MutableState<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = mutableStateOf("")
        data.value = intent.getStringExtra("url").toString()

      /*  var webViewClients = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("WebView", "Page started loading: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Page finished loading: $url")
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("WebView", "Error loading page: $description")
            }
        }
*/
        Log.i("Data", "$data")
        setContent {
            WebViewComponent(data, this@WebClass)
          /*  AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = webViewClients
                    }
                },
                update = { webView ->
                    webView.loadUrl(data.toString())
                }
            )*/
        }
    }
}

@Composable
fun WebViewComponent(url: MutableState<String>, webClass: WebClass) {
    val webView = WebView(LocalContext.current).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = true
        webViewClient = MyWebViewClient(url, webClass)
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { webView }
    ) { webView ->
        webView.loadUrl(url.value)
    }
}

class MyWebViewClient(val url: MutableState<String>, val context: WebClass) : WebViewClient() {
    private var isActivityFinished = false
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        // Check if the URL matches the specific link you want to handle
        if (isActivityFinished) {
            return true // Prevent further processing
        }
        Log.i("URL", "${url.value}")
        if (url.value.contains("https://testing.fionpay.com/payment") || url.value.contains("https://testing.fionpay.com/withdraw")) {
            // Go back to the previous activity
            //context.startActivity(Intent(context, MainActivity::class.java))
            Handler(Looper.getMainLooper()).postDelayed({
                isActivityFinished = true
                context.finish()
            }, 300)


            return true // Indicates that the WebView should not load the URL
        }
        // Allow the WebView to load other URLs
        return super.shouldOverrideUrlLoading(view, request)
    }
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        val pageTitle = view?.title
        Log.i("URL", "${url}  title: ${pageTitle}")

        Log.i("URL", "${url}")
/*        if (url?.contains("https://testing.fionpay.com/payment") == true || url?.contains("https://testing.fionpay.com/withdraw") == true) {
            // Go back to the previous activity
            //context.startActivity(Intent(context, MainActivity::class.java))
            Handler(Looper.getMainLooper()).postDelayed({
                isActivityFinished = true
                context.finish()
            }, 300)
            // Indicates that the WebView should not load the URL
        }*/
        // Handle page finished loading
    }

    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        // Handle error loading page
    }
}