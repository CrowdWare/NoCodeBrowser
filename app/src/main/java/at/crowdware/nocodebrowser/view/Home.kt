package at.crowdware.nocodebrowser.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.crowdware.nocodebrowser.ui.theme.NoCodeBrowserTheme
import at.crowdware.nocodelib.ButtonElement
import at.crowdware.nocodelib.ColumnElement
import at.crowdware.nocodelib.ImageElement
import at.crowdware.nocodelib.MarkdownElement
import at.crowdware.nocodelib.Page
import at.crowdware.nocodelib.PageParser
import at.crowdware.nocodelib.RowElement
import at.crowdware.nocodelib.SoundElement
import at.crowdware.nocodelib.SpacerElement
import at.crowdware.nocodelib.TextElement
import at.crowdware.nocodelib.UIElement
import at.crowdware.nocodelib.VideoElement
import at.crowdware.nocodelib.YoutubeElement


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

@Composable
fun Home(name: String, modifier: Modifier = Modifier) {
    // parsing
    var parseError = ""
    val xml = "<page><column padding='16'><markdown># Title</markdown><spacer height='8'/><button label='about'/></column></page>"
    val parsedPage = try {
        if (xml.isEmpty()) {
            parseError = "no page loaded"
            null
        } else {
            if(xml.contains("<page")) {
                val pageParser = PageParser()
                val page = pageParser.parse(xml)
                println("page: $page")
                if (page.elements.isEmpty()) {
                    parseError = "page is empty"
                    null
                } else {
                    page
                }
            } else if (xml.contains("<app")) {
                println("app loaded")
                null
            } else {
                parseError = "no page loaded"
                null
            }
        }
    } catch (e: Exception) {
        parseError = e.message ?: ""
        println("Error parsing xml: ${e.message}")
        null
    }

    // rendering
    if (parsedPage != null) {
        println("padding: ${parsedPage.padding.left}")
        Row (modifier = Modifier
            .padding(start = parsedPage.padding.left.dp, top = parsedPage.padding.top.dp, bottom = parsedPage.padding.bottom.dp, end = parsedPage.padding.right.dp )
            .fillMaxSize()
            .background(color = hexToColor(parsedPage.backgroundColor))) {
            RenderPage(parsedPage)
        }
    } else {
        Row{
            Text(text = parseError, color = Color.Red)
        }
    }
}

@Composable
fun RenderPage(page: Page) {
    for (element in page.elements) {
        RenderUIElement(element)
    }
}

@Composable
fun RenderUIElement(element: UIElement) {
    when (element) {
        is TextElement -> {
            Text(
                text = element.text,
                style = TextStyle(color = hexToColor(element.color))
            )
        }
        is MarkdownElement -> {
            val parsedMarkdown = parseMarkdown(element.text)

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

}
@Composable
fun dynamicSoundfromAssets(filename: String) {

}
@Composable
fun dynamicVideofromAssets(filename: String, height: Int) {

}
@Composable
fun dynamicYoutube(height: Int) {

}

fun parseMarkdown(markdown: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val lines = markdown.split("\n") // Verarbeite alle Zeilen

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

        // Füge Zeilenumbrüche nur zwischen den Zeilen hinzu, nicht am Ende
        if (i < lines.size - 1) {
            builder.append("\n")
        }
    }

    return builder.toAnnotatedString()
}