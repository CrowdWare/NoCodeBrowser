package at.crowdware.nocodebrowser.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import at.crowdware.nocodebrowser.MainActivity
import at.crowdware.nocodebrowser.parsePage
import at.crowdware.nocodebrowser.ui.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoadPage(name: String, navhostBackground: MutableState<Color>, mainActivity: MainActivity) {
    val context = LocalContext.current
    var page by remember { mutableStateOf(Page(color="#FFFFFF", backgroundColor = "#000000", padding = Padding(0,0,0,0), elements = mutableListOf()))}
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val smlContent = withContext(Dispatchers.IO) {
            if (context is MainActivity) {
                val url = "https://nocode.crowdware.at/sml/$name.sml"
                context.contentLoader.downloadSml(url)
            } else
                null
        }
        if (smlContent != null) {
            page = parsePage(smlContent)
        } else {
            println("load failed...")
            page = parsePage("Page { Column { padding: \"16\" Text { color: \"#FF0000\" fontSize: 18 text:\"An error occurred loading the home page.\"}}}")
        }
        isLoading = false
    }
    if (isLoading) {
        Box (modifier = Modifier.fillMaxSize().background(color = navhostBackground.value)){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        if (page != null) {
            val padding = page.padding
            val bgColor = page.backgroundColor
            navhostBackground.value = hexToColor(bgColor)
            Row(
                modifier = Modifier
                    .padding(
                        start = padding.left.dp,
                        top = padding.top.dp,
                        bottom = padding.bottom.dp,
                        end = padding.right.dp
                    )
                    .fillMaxSize()
                    .background(color = hexToColor(bgColor))
            ) {
                RenderPage(page!!, navhostBackground, mainActivity)
            }
        }
    }
}

@Composable
fun RenderPage(page: Page, navhostBackground: MutableState<Color>, mainActivity: MainActivity) {
    for (element in page.elements) {
        RenderElement(element, navhostBackground, mainActivity)
    }
}

@Composable
fun RenderElement(
    element: UIElement,
    navhostBackground: MutableState<Color>,
    mainActivity: MainActivity
) {
    when (element) {
        is UIElement.Zero -> {}
        is UIElement.ColumnElement -> {
            Column (modifier = Modifier.padding(
                top = element.padding.top.dp,
                bottom = element.padding.bottom.dp,
                start = element.padding.left.dp,
                end = element.padding.right.dp
            )) {
                for (ele in element.uiElements) {
                    RenderElement(ele, navhostBackground, mainActivity)
                }
            }
        }
        is UIElement.RowElement -> {
            Row (modifier = Modifier.padding(
                top = element.padding.top.dp,
                bottom = element.padding.bottom.dp,
                start = element.padding.left.dp,
                end = element.padding.right.dp
            )) {
                for (ele in element.uiElements) {
                    RenderElement(ele, navhostBackground, mainActivity)
                }
            }
        }
        is UIElement.TextElement -> {
            Text(
                text = element.text.trim(),
                fontSize = element.fontSize,
                style = TextStyle(color = element.color)
            )
        }
        is UIElement.MarkdownElement -> {
            val parsedMarkdown = parseMarkdown(element.text.trim())
            Text(
                text = parsedMarkdown,
                style = TextStyle(color = hexToColor(element.color))
            )
        }
        is UIElement.ButtonElement -> {
            Button(modifier = Modifier.fillMaxWidth(), onClick =  { handleButtonClick(element.link, navhostBackground, mainActivity) }) {
                Text(text = element.label)
            }
        }
        is UIElement.ImageElement -> {
            dynamicImageFromAssets(filename = element.src, element.scale, element.link)
        }
        is UIElement.VideoElement -> {
            dynamicVideofromAssets(element.src, element.height)
        }
        is UIElement.SoundElement -> {
            dynamicSoundfromAssets(element.src)
        }
        is UIElement.SpacerElement -> {
            Spacer(modifier = Modifier.height(element.height.dp))
        }
        is UIElement.YoutubeElement -> {
            dynamicYoutube(element.height)
        }
    }
}

fun handleButtonClick(
    link: String,
    navhostBackground: MutableState<Color>,
    mainActivity: MainActivity
) {
    when {
        link.startsWith("page:") -> {
            val pageId = link.removePrefix("page:")
            mainActivity.loadPage(pageId)
        }
        link.startsWith("web:") -> {
            val url = link.removePrefix("web:")
            mainActivity.openWebPage(url)
        }
        else -> {
            println("Unknown link type: $link")
        }
    }
}


@Composable
fun dynamicImageFromAssets(filename: String, scale: String, link: String) {
    // TODO: load Image from webserver
}
@Composable
fun dynamicSoundfromAssets(filename: String) {
    // TODO: load Sound from webserver
}
@Composable
fun dynamicVideofromAssets(filename: String, height: Int) {
    // TODO: load Video from webserver
}
@Composable
fun dynamicYoutube(height: Int) {
    // TODO: load Youtube
}