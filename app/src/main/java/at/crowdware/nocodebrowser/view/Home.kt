package at.crowdware.nocodebrowser.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.crowdware.nocodelib.Padding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory

fun hexToColor(hex: String): Color {
    val color = hex.trimStart('#')
    return when (color.length) {
        6 -> {
            // Hex without alpha (e.g., "RRGGBB")
            val r = color.substring(0, 2).toIntOrNull(16) ?: return Color.Black
            val g = color.substring(2, 4).toIntOrNull(16) ?: return Color.Black
            val b = color.substring(4, 6).toIntOrNull(16) ?: return Color.Black
            Color(r, g, b)
        }
        8 -> {
            // Hex with alpha (e.g., "AARRGGBB")
            val a = color.substring(0, 2).toIntOrNull(16) ?: return Color.Black
            val r = color.substring(2, 4).toIntOrNull(16) ?: return Color.Black
            val g = color.substring(4, 6).toIntOrNull(16) ?: return Color.Black
            val b = color.substring(6, 8).toIntOrNull(16) ?: return Color.Black
            Color(r, g, b, a)
        }
        else -> Color.Black
    }
}

fun downloadXml(url: String): String? {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    return try {
        val response: Response = client.newCall(request).execute()
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

fun getRootElement(xml: String): Element? {
    return try {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val inputStream = ByteArrayInputStream(xml.toByteArray(Charsets.UTF_8))
        val document: Document = builder.parse(inputStream)
        document.documentElement.normalize()
        document.documentElement
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Home(name: String, modifier: Modifier = Modifier) {
    var page by remember { mutableStateOf(getRootElement("<page><column padding='16'><text color='#FFFFFF'>One moment, page loading...</text></column></page>")) }

    LaunchedEffect(Unit) {
        val xmlContent = withContext(Dispatchers.IO) {
            downloadXml("https://nocode.crowdware.at/pages/home.xml")
        }
        if (xmlContent != null) {
            page = getRootElement(xmlContent)
        } else {
            page = getRootElement("<page><column padding='16'><text color='#FF0000'>An error occurred loading the home page.</text></column></page>")
        }
    }

    if(page != null) {
        val padding = parsePadding( page!!.getAttribute("padding"))
        val bgColor = page!!.getAttribute("backgroundColor")
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
            RenderElement(page!!) }
    }
}

fun parsePadding(padding: String): Padding {
    val paddingValues = padding.split(" ").mapNotNull { it.toIntOrNull() }

    return when (paddingValues.size) {
        1 -> Padding(paddingValues[0], paddingValues[0], paddingValues[0], paddingValues[0]) // Alle Seiten gleich
        2 -> Padding(paddingValues[0], paddingValues[1], paddingValues[0], paddingValues[1]) // Vertikal und Horizontal gleich
        4 -> Padding(paddingValues[0], paddingValues[1], paddingValues[2], paddingValues[3]) // Oben, Rechts, Unten, Links
        else -> Padding(0, 0, 0, 0)
    }
}
/*
@Composable
fun RenderUIElement(element: Element) {
    when (element) {
        is TextElement -> {
            Text(
                text = element.text,
                style = TextStyle(color = hexToColor(element.color))
            )
        }
        is MarkdownElement -> {
            val parsedMarkdown = parseMarkdown(element.text)
            println("sax: $parsedMarkdown")
            Text(
                text = parsedMarkdown,
                style = TextStyle(color = hexToColor(element.color))
            )
        }
        is ButtonElement -> {
            Button(modifier = Modifier.fillMaxWidth(), onClick =  { handleButtonClick(element.link) }) {
                Text(text = element.label)
            }
        }
        is ColumnElement -> {
            Column(modifier = Modifier.padding(
                top = element.padding.top.dp,
                bottom = element.padding.bottom.dp,
                start = element.padding.left.dp,
                end = element.padding.right.dp
            )) {
                for (childElement in element.uiElements) {
                    RenderUIElement(childElement)
                }
            }
        }
        is RowElement -> {
            Row(modifier = Modifier.padding(
                top = element.padding.top.dp,
                bottom = element.padding.bottom.dp,
                start = element.padding.left.dp,
                end = element.padding.right.dp
            )) {
                for (childElement in element.uiElements) {
                    RenderUIElement(childElement)
                }
            }
        }
        is ImageElement -> {
            dynamicImageFromAssets(filename = element.src, element.scale, element.link)
        }
        is SoundElement -> {
            dynamicSoundfromAssets(element.src)
        }
        is SpacerElement -> {
            Spacer(modifier = Modifier.height(element.height.dp))
        }
        is VideoElement -> {
            dynamicVideofromAssets(element.src, element.height)
        }
        is YoutubeElement -> {
            dynamicYoutube(element.height)
        }
        else -> {
            // Hier können andere Elemente behandelt werden
            println("Unknown element: $element")
        }
    }
}
*/

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

fun parseMarkdown(markdown: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val lines = markdown.split("\n")

    for (i in lines.indices) {
        val line = lines[i]
        var j = 0
        while (j < line.length) {
            when {
                line.startsWith("###### ", j) -> {
                    builder.withStyle(SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)) {
                        append(line.removePrefix("###### ").trim())
                    }
                    j = line.length
                }
                line.startsWith("##### ", j) -> {
                    builder.withStyle(SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
                        append(line.removePrefix("##### ").trim())
                    }
                    j = line.length
                }
                line.startsWith("#### ", j) -> {
                    builder.withStyle(SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)) {
                        append(line.removePrefix("#### ").trim())
                    }
                    j = line.length
                }
                line.startsWith("### ", j) -> {
                    builder.withStyle(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)) {
                        append(line.removePrefix("### ").trim())
                    }
                    j = line.length
                }
                line.startsWith("## ", j) -> {
                    builder.withStyle(SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)) {
                        append(line.removePrefix("## ").trim())
                    }
                    j = line.length
                }
                line.startsWith("# ", j) -> {
                    builder.withStyle(SpanStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold)) {
                        append(line.removePrefix("# ").trim())
                    }
                    j = line.length
                }
                line.startsWith("***", j) -> {
                    val endIndex = line.indexOf("***", j + 3)
                    if (endIndex != -1) {
                        builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                            append(line.substring(j + 3, endIndex).trim())
                        }
                        j = endIndex + 3
                    } else {
                        builder.append("***")
                        j += 3
                    }
                }
                line.startsWith("**", j) -> {
                    val endIndex = line.indexOf("**", j + 2)
                    if (endIndex != -1) {
                        builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(line.substring(j + 2, endIndex).trim())
                        }
                        j = endIndex + 2
                    } else {
                        builder.append("**")
                        j += 2
                    }
                }
                line.startsWith("*", j) -> {
                    val endIndex = line.indexOf("*", j + 1)
                    if (endIndex != -1) {
                        builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(line.substring(j + 1, endIndex).trim())
                        }
                        j = endIndex + 1
                    } else {
                        builder.append("*")
                        j += 1
                    }
                }
                line.startsWith("~~", j) -> {
                    val endIndex = line.indexOf("~~", j + 2)
                    if (endIndex != -1) {
                        builder.withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                            append(line.substring(j + 2, endIndex).trim())
                        }
                        j = endIndex + 2
                    } else {
                        builder.append("~~")
                        j += 2
                    }
                }
                line.startsWith("(c)", j) || line.startsWith("(C)", j) -> {
                    builder.append("©")
                    j += 3
                }
                line.startsWith("(r)", j) || line.startsWith("(R)", j) -> {
                    builder.append("®")
                    j += 3
                }
                line.startsWith("(tm)", j) || line.startsWith("(TM)", j) -> {
                    builder.append("™")
                    j += 4
                }
                else -> {
                    builder.append(line[j])
                    j++
                }
            }
        }

        if (i < lines.size - 1) {
            builder.append("\n")
        }
    }

    return builder.toAnnotatedString()
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
        "image" -> {
            dynamicImageFromAssets(filename = element.getAttribute("srv"), element.getAttribute("scale"), element.getAttribute("link"))
        }
    }
}