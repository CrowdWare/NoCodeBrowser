/****************************************************************************
 * Copyright (C) 2024 CrowdWare
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
package at.crowdware.nocodebrowser.ui.pages

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.crowdware.nocodebrowser.MainActivity
import at.crowdware.nocodebrowser.R
import at.crowdware.nocodebrowser.logic.LocaleManager
import at.crowdware.nocodebrowser.ui.theme.OnPrimary
import at.crowdware.nocodebrowser.ui.theme.Primary
import at.crowdware.nocodebrowser.ui.widgets.DropDownListbox
import at.crowdware.shift.ui.widgets.rememberDropDownListboxStateHolder


@Composable
fun Settings() {
    val context = LocalContext.current
    val title = remember { mutableStateOf("") }
    val url = remember { mutableStateOf("") }
    var addButtonEnabled by remember { mutableStateOf(false) }
    var titleChanged = remember { mutableStateOf(false) }
    var urlChanged = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    //region vars for the DropDownlistbox
    val languages = LocaleManager.getLanguages()
    val index = LocaleManager.getLanguageIndex()
    val currentActivity = LocalContext.current as? Activity
    val onSelectedIndexChanged: (Int) -> Unit = { idx ->
        LocaleManager.setLocale(context, idx)
        currentActivity?.recreate()
    }
    val stateHolderLanguage = rememberDropDownListboxStateHolder(languages, index, onSelectedIndexChanged)
    //endregion

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.personal_data), fontWeight = FontWeight.Bold,
            style = TextStyle(fontSize = 18.sp),
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        DropDownListbox(
            label = stringResource(R.string.select_preferred_language),
            stateHolder = stateHolderLanguage,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.links), fontWeight = FontWeight.Bold,
            style = TextStyle(fontSize = 18.sp),
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = title.value,
            label = {Text(stringResource(R.string.title),)},
            onValueChange = {it ->
                title.value = it
                titleChanged.value = true
                addButtonEnabled = title.value.isNotEmpty() && url.value.isNotEmpty()
            })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = url.value,
            label = {Text(stringResource(R.string.url),)},
            onValueChange = {it ->
                url.value = it
                urlChanged.value = true
                addButtonEnabled = title.value.isNotEmpty() && url.value.isNotEmpty()
            })
        Spacer(modifier = Modifier.height(8.dp))
        Button(colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = OnPrimary
        ),onClick = {
            if (context is MainActivity) {
                context.contentLoader.addLink(title.value, url.value)
            }
            title.value = ""
            url.value = ""
            addButtonEnabled = false
        }, modifier = Modifier.fillMaxWidth(), enabled = addButtonEnabled) {
            Text(stringResource(R.string.add_link), fontWeight = FontWeight.Bold,
                style = TextStyle(fontSize = 18.sp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SettingsPreview() {
    Settings()
}