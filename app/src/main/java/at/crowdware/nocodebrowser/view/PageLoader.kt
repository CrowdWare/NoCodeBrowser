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
import at.crowdware.nocodebrowser.ui.getRootElement
import at.crowdware.nocodebrowser.ui.hexToColor
import at.crowdware.nocodebrowser.ui.parseMarkdown
import at.crowdware.nocodebrowser.ui.parsePadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.w3c.dom.Node



@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoadPage(name: String, navhostBackground: MutableState<Color>) {
    var page by remember { mutableStateOf(getRootElement("<page><column padding='16'><text color='#FFFFFF'>One moment, page loading...</text></column></page>")) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val xmlContent = withContext(Dispatchers.IO) {
            if (context is MainActivity) {
                context.downloadXml("https://nocode.crowdware.at/pages/$name.xml")
            } else
                null
        }
        if (xmlContent != null) {
            page = getRootElement(xmlContent)
        } else {
            page =
                getRootElement("<page><column padding='16'><text color='#FF0000'>An error occurred loading the home page.</text></column></page>")
        }
        isLoading = false
    }
    if (isLoading) {
        Box (modifier = Modifier.fillMaxSize().background(color = navhostBackground.value)){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        if (page != null) {
            val padding = parsePadding(page!!.getAttribute("padding"))
            val bgColor = page!!.getAttribute("backgroundColor")
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
                RenderElement(page!!)
            }
        }
    }
}

@Composable
fun RenderElement(element: Element) {
    when(element.tagName) {
        "page" -> {
            for (i in 0 until element.childNodes.length) {
                val childNode: Node = element.childNodes.item(i)
                if (childNode.nodeType == Node.ELEMENT_NODE) {
                    val childElement = childNode as Element
                    RenderElement(childElement)
                }
            }
        }
        "column" -> {
            val padding = parsePadding(element.getAttribute("padding"))
            Column(modifier = Modifier.padding(
                top = padding.top.dp,
                bottom = padding.bottom.dp,
                start = padding.left.dp,
                end = padding.right.dp
            )) {
                for (i in 0 until element.childNodes.length) {
                    val childNode: Node = element.childNodes.item(i)
                    if (childNode.nodeType == Node.ELEMENT_NODE) {
                        val childElement = childNode as Element
                        RenderElement(childElement)
                    }
                }
            }
        }
        "row" -> {
            val padding = parsePadding(element.getAttribute("padding"))
            Row(modifier = Modifier.padding(
                top = padding.top.dp,
                bottom = padding.bottom.dp,
                start = padding.left.dp,
                end = padding.right.dp
            )) {
                for (i in 0 until element.childNodes.length) {
                    val childNode: Node = element.childNodes.item(i)
                    if (childNode.nodeType == Node.ELEMENT_NODE) {
                        val childElement = childNode as Element
                        RenderElement(childElement)
                    }
                }
            }
        }
        "text" -> {
            Text(

                text = element.textContent.trim(),
                style = TextStyle(color = hexToColor(element.getAttribute("color")))
            )
        }
        "markdown" -> {
            val parsedMarkdown = parseMarkdown(element.textContent.trim())
            Text(
                text = parsedMarkdown,
                style = TextStyle(color = hexToColor(element.getAttribute("color")))
            )
        }
        "button" -> {
            Button(modifier = Modifier.fillMaxWidth(), onClick =  { handleButtonClick(element.getAttribute("link")) }) {
                Text(text = element.getAttribute("label"))
            }
        }
        "image" -> {
            dynamicImageFromAssets(filename = element.getAttribute("srv"), element.getAttribute("scale"), element.getAttribute("link"))
        }
        "sound" -> {
            dynamicSoundfromAssets(element.getAttribute("src"))
        }
        "spacer" -> {
            Spacer(modifier = Modifier.height(element.getAttribute("height").toInt().dp))
        }
        "video" -> {
            dynamicVideofromAssets(element.getAttribute("src"), element.getAttribute("height").toInt())
        }
        "youtube" -> {
            dynamicYoutube(element.getAttribute("height").toInt())
        }
    }
}

fun handleButtonClick(link: String) {
    when {
        link.startsWith("page:") -> {
            val pageId = link.removePrefix("page:")
            //loadPage(pageId)
        }
        link.startsWith("web:") -> {
            val url = link.removePrefix("web:")
            //openWebPage(url)
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