package at.crowdware.nocodebrowser

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import at.crowdware.nocodebrowser.logic.LocaleManager
import at.crowdware.nocodebrowser.ui.App
import at.crowdware.nocodebrowser.ui.theme.NoCodeBrowserTheme
import at.crowdware.nocodebrowser.ui.widgets.NavigationItem
import at.crowdware.nocodebrowser.ui.widgets.NavigationView
import at.crowdware.nocodebrowser.utils.ContentLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    val contentLoader = ContentLoader()
    private var app: App? by mutableStateOf(null)
    private var loading by mutableStateOf(false)
    private val url = "https://nocode.crowdware.at/sml/app.sml"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this

        contentLoader.init(this)
        lifecycleScope.launch(Dispatchers.Main) {
            // load the dynamic app, we can change the content on the web server
            if (!loading) {
                loading = true
                app = contentLoader.loadApp(url)
            }
            if (app != null) {
                enableEdgeToEdge()
                setContent {
                    NoCodeBrowserTheme {
                        LocaleManager.init(applicationContext, resources)

                        Scaffold(modifier = Modifier.fillMaxSize()) {  _ ->
                            val list = mutableListOf(
                                NavigationItem("app.home", contentLoader.appUrl, Icons.Default.Home, stringResource(R.string.navigation_home)),
                                NavigationItem("app.about", contentLoader.appUrl, Icons.Default.AccountCircle, stringResource(R.string.navigation_about)),
                                NavigationItem("app.settings", "", Icons.Default.Settings, stringResource(R.string.settings)),
                            )
                            if(contentLoader.links.isNotEmpty())
                                list.add(NavigationItem("divider"))
                            for(link in contentLoader.links) {
                                list.add(NavigationItem(link.titel, link.url, Icons.Default.Star, link.titel))
                            }

                            // navigation targets which are not listed in the drawer
                            for (file in app!!.deployment.files) {
                                if (file.path.endsWith(".sml")) {
                                    list.add(NavigationItem(file.path.substringBefore(".sml"), contentLoader.appUrl))
                                }
                            }

                            NavigationView(list, context)
                        }
                    }
                }
            }
        }
    }

    fun openWebPage( url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleManager.wrapContext(newBase!!))
    }
}

