/****************************************************************************
 * Copyright (C) 2025 CrowdWare
 *
 * This file is part of NoCodeBrowser.
 *
 *  NoCodeBrowser is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  NoCodeBrowser is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with NoCodeBrowser.  If not, see <http://www.gnu.org/licenses/>.
 *
 ****************************************************************************/
package at.crowdware.nocodebrowser

import android.annotation.SuppressLint
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.crowdware.nocodelibmobile.BaseComposeActivity
import at.crowdware.nocodelibmobile.logic.LocaleManager
import at.crowdware.nocodelibmobile.ui.theme.NoCodeLibMobileTheme
import at.crowdware.nocodelibmobile.ui.widgets.NavigationItem
import at.crowdware.nocodelibmobile.ui.widgets.NavigationView
import at.crowdware.nocodelibmobile.utils.LoadPage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : BaseComposeActivity() {

    override fun getBaseUrl(): String {
        return "https://crowdware.github.io/NoCodeBrowser/app.sml"
    }

    @Composable
    override fun ComposeRoot() {
        val context = this
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        NoCodeLibMobileTheme(app!!.theme) {
            LocaleManager.init(applicationContext, resources)
            println("AppId: ${app!!.id}")
            if (app!!.id == "at.crowdware.nocodebrowser") {
                println("we are in")
                // in the local app we use Scaffold and the navigation drawer
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    val list = mutableListOf(
                        NavigationItem(
                            "app.home",
                            contentLoader.appUrl,
                            Icons.Default.Home,
                            stringResource(R.string.navigation_home)
                        ),
                        NavigationItem(
                            "app.books",
                            contentLoader.appUrl,
                            Icons.Default.ShoppingCart,
                            stringResource(R.string.navigation_books)
                        ),
                        NavigationItem(
                            "app.about",
                            contentLoader.appUrl,
                            Icons.Default.AccountCircle,
                            stringResource(R.string.navigation_about)
                        ),
                        NavigationItem(
                            "app.settings",
                            "",
                            Icons.Default.Settings,
                            stringResource(R.string.settings)
                        ),
                    )
                    if (contentLoader.links.isNotEmpty())
                        list.add(NavigationItem("divider"))
                    for (link in contentLoader.links) {
                        list.add(
                            NavigationItem(
                                "home",
                                link.url,
                                Icons.Default.Star,
                                link.titel
                            )
                        )
                    }

                    // navigation targets which are not listed in the drawer
                    for (file in app!!.deployment.files) {
                        if (file.path.endsWith(".sml")) {
                            list.add(
                                NavigationItem(
                                    file.path.substringBefore(".sml"),
                                    contentLoader.appUrl,
                                )
                            )
                        }
                    }
                    println("about to draw navi")
                    NavigationView(list, context, "NoCodeBrowser")
                }
            } else {
                // if the external app is loaded we only render the app
                val navController = rememberNavController()
                val color = remember { mutableStateOf(Color.Unspecified) }
                val list = mutableListOf<String>()

                // navigation targets which are not listed in the drawer
                for (file in app!!.deployment.files) {
                    if (file.path.endsWith(".sml")) {
                        list.add(file.path.substringBefore(".sml"))
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (list.isNotEmpty()) {
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier
                                .background(color = color.value)
                                .systemBarsPadding()
                        ) {
                            for (index in list.indices) {
                                composable(list[index]) {
                                    LoadPage(
                                        list[index],
                                        color,
                                        context,
                                        navController
                                    )
                                }
                            }
                        }
                    } else {
                        Text("The list of pages is empty. Maybe the deployment descriptor list has not been added to app.sml.")
                    }
                }
            }
        }
    }
}

