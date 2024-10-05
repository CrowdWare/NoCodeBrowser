/****************************************************************************
 * Copyright (C) 2023 CrowdWare
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
package at.crowdware.nocodebrowser.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.crowdware.nocodebrowser.MainActivity
import at.crowdware.nocodebrowser.R
import at.crowdware.nocodebrowser.ui.theme.OnPrimary
import at.crowdware.nocodebrowser.ui.theme.Primary
import at.crowdware.nocodebrowser.view.Home
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NavigationView(items: MutableList<NavigationItem>, mainActivity: MainActivity) {
    val navController = rememberNavController()
    val selectedItem = remember { mutableStateOf("home") }

    NavigationManager.setNavController(navController)
    val title = remember { mutableStateOf("NoCodeBrowser") }
    var navTarget = remember { mutableStateOf("") }
    val context = LocalContext.current
    val pluginName = remember { mutableStateOf("App") }

    NavHost(navController = navController, startDestination = "home") {
        for (index in items.indices) {
            composable(items[index].id) {
                when (items[index].id) {
                    "home" -> {
                        title.value = "NoCodeBrowser";navTarget.value = ""
                    }

                    "settings" -> {
                        title.value = stringResource(R.string.settings);navTarget.value = ""
                    }

                    else -> {
                        title.value = items[index].text;navTarget.value = ""
                    }
                }
                runCatching {
                    pluginName.value = "App"
                    NavigationDrawer(items, selectedItem, title.value, navTarget.value) {
                        when (items[index].id) {
                            // have a look at MainActivity for navigation
                            "home" -> Home("home")
                            //"settings" -> Settings()

                            else -> {

                            }
                        }
                    }
                }.onFailure { exception ->
                    println("${pluginName.value} has crashed: ${exception.message}")
                    writeCrashReportToFile(context, exception, pluginName.value)
                }
            }
        }
    }
}

fun writeCrashReportToFile(context: Context, exception: Throwable, pluginName: String) {
    var crashReport = "Crash information for "
    if (pluginName == "App") {
        crashReport += "the app: ${exception.message}\n${exception.stackTraceToString()}"
    }else {
        crashReport += "<$pluginName>: ${exception.message}\n${exception.stackTraceToString()}"
    }


    val crashFile = File(context.filesDir, "crash_report.txt")

    try {
        val fileWriter = FileWriter(crashFile, true)
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.append(crashReport)
        bufferedWriter.newLine()
        bufferedWriter.close()
        fileWriter.close()
    } catch (e: IOException) {
        // app is already crashing
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    items: List<NavigationItem>,
    selectedItem: MutableState<String>,
    title: String,
    navTarget: String = "",
    content: @Composable () -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    if (openDialog.value) {
        About(
            openDialog = openDialog.value,
            onDismiss = { openDialog.value = false })
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerSheet(drawerState, items, selectedItem) },
        content = {
            Column() {
                CenterAlignedTopAppBar(
                    title = { Text(title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Primary,
                        titleContentColor = OnPrimary,
                        navigationIconContentColor = OnPrimary,
                        actionIconContentColor = OnPrimary
                    ),
                    navigationIcon = {
                        if(navTarget != "") {
                            IconButton(onClick = { NavigationManager.navigate(navTarget)}) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = null)
                            }
                        } else {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = null)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { openDialog.value = true }) {
                            Icon(
                                Icons.Filled.MoreVert,
                                contentDescription = stringResource(R.string.navigation_about)
                            )
                        }
                    }
                )
                content()
            }
        }
    )
}

@Composable
fun About(openDialog: Boolean, onDismiss: () -> Unit) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(R.string.about_shift)) },
            text = {
                Text(
                    stringResource(R.string.about_dialog_text)
                )
            },
            confirmButton = { TextButton(onClick = onDismiss ) { Text("OK") } },
            dismissButton = {}
        )
    }
}