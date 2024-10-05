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


import at.crowdware.nocodelib.PageParser

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoCodeBrowserTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {  innerPadding ->
                    val list = mutableListOf(
                        NavigationItem("home", Icons.Default.Home, stringResource(R.string.navigation_home)),
                        NavigationItem("settings", Icons.Default.Settings, stringResource(R.string.settings)),
                        NavigationItem("divider")
                    )

                    // navigation targets which are not listed in the drawer
                    list.add(NavigationItem(id="receive_gratitude_qrcode"))
                    list.add(NavigationItem(id="receive_gratitude"))
                    list.add(NavigationItem(id="give_gratitude"))
                    list.add(NavigationItem(id="give_gratitude_qrcode"))
                    list.add(NavigationItem(id="scan_agreement"))
                    list.add(NavigationItem(id="plugin_settings"))
                    NavigationView(list, this)
                }
            }
        }
    }
}

