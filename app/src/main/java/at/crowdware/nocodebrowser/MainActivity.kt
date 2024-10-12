package at.crowdware.nocodebrowser

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import at.crowdware.nocodebrowser.ui.theme.NoCodeBrowserTheme
import at.crowdware.nocodebrowser.ui.widgets.NavigationItem
import at.crowdware.nocodebrowser.ui.widgets.NavigationView
import at.crowdware.nocodebrowser.utils.ContentLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    val contentLoader = ContentLoader()
    var pageId: String by mutableStateOf("home")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contentLoader.init(this)

        lifecycleScope.launch(Dispatchers.Main) {
            val app = contentLoader.loadApp("https://nocode.crowdware.at/sml/app.sml")
            if (app != null) {
                println("App: ${app}")
            } else {
                println("Unable to open app")
            }
        }
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
                    list.add(NavigationItem(id="video"))
                    NavigationView(list, this, pageId)
                }
            }
        }
    }

    fun openWebPage( url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
    fun loadPage(pId: String) {
        pageId = pId
    }
}

