package at.crowdware.nocodebrowser

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import at.crowdware.nocodebrowser.ui.theme.NoCodeBrowserTheme
import at.crowdware.nocodebrowser.ui.widgets.NavigationItem
import at.crowdware.nocodebrowser.ui.widgets.NavigationView
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    val cacheSize = 10 * 1024 * 1024 // 10 MB
    private lateinit var cache: Cache
    private lateinit var okHttpClient: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cache = Cache(this.cacheDir, cacheSize.toLong())
        okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .build()


        enableEdgeToEdge()
        setContent {
            NoCodeBrowserTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {  _ ->
                    val list = mutableListOf(
                        NavigationItem("home", Icons.Default.Home, stringResource(R.string.navigation_home)),
                        NavigationItem("about", Icons.Default.Home, stringResource(R.string.navigation_about)),
                        NavigationItem("settings", Icons.Default.Settings, stringResource(R.string.settings)),
                        NavigationItem("divider")
                    )

                    // navigation targets which are not listed in the drawer
                    //list.add(NavigationItem(id="receive_gratitude_qrcode"))
                    //list.add(NavigationItem(id="receive_gratitude"))
                    //list.add(NavigationItem(id="give_gratitude"))
                    //list.add(NavigationItem(id="give_gratitude_qrcode"))
                    //list.add(NavigationItem(id="scan_agreement"))
                    list.add(NavigationItem(id="video"))
                    NavigationView(list, this)
                }
            }
        }
    }

    fun downloadXml(url: String): String? {
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            val response: Response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

