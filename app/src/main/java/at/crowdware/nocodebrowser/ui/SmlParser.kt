package at.crowdware.nocodebrowser

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import at.crowdware.nocodebrowser.ui.App
import at.crowdware.nocodebrowser.ui.DeploymentElement
import at.crowdware.nocodebrowser.ui.FileElement
import at.crowdware.nocodebrowser.ui.ItemElement
import at.crowdware.nocodebrowser.ui.NavigationElement
import at.crowdware.nocodebrowser.ui.UIElement.ButtonElement
import at.crowdware.nocodebrowser.ui.UIElement.ColumnElement
import at.crowdware.nocodebrowser.ui.UIElement.ImageElement
import at.crowdware.nocodebrowser.ui.UIElement.MarkdownElement
import at.crowdware.nocodebrowser.ui.Padding
import at.crowdware.nocodebrowser.ui.Page
import at.crowdware.nocodebrowser.ui.UIElement.RowElement
import at.crowdware.nocodebrowser.ui.UIElement.SoundElement
import at.crowdware.nocodebrowser.ui.UIElement.SpacerElement
import at.crowdware.nocodebrowser.ui.UIElement.TextElement
import at.crowdware.nocodebrowser.ui.UIElement
import at.crowdware.nocodebrowser.ui.UIElement.VideoElement
import at.crowdware.nocodebrowser.ui.UIElement.YoutubeElement
import at.crowdware.nocodebrowser.ui.hexToColor
import at.crowdware.nocodebrowser.ui.parsePadding
import com.github.h0tk3y.betterParse.combinators.and
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.oneOrMore
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.zeroOrMore
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import com.github.h0tk3y.betterParse.utils.Tuple7
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


sealed class PropertyValue {
    data class StringValue(val value: String) : PropertyValue()
    data class IntValue(val value: Int) : PropertyValue()
    data class FloatValue(val value: Float) : PropertyValue()
}

val identifier: Token = regexToken("[a-zA-Z_][a-zA-Z0-9_]*")
val lBrace: Token = literalToken("{")
val rBrace: Token = literalToken("}")
val colon: Token = literalToken(":")
val stringLiteral: Token = regexToken("\"[^\"]*\"")
val whitespace: Token = regexToken("\\s+")
val integerLiteral: Token = regexToken("\\d+")
val floatLiteral = regexToken("\\d+\\.\\d+")

val lineComment: Token = regexToken("//.*")
val blockComment: Token = regexToken(Regex("/\\*[\\s\\S]*?\\*/", RegexOption.DOT_MATCHES_ALL))

object SmlGrammar : Grammar<List<Any>>() {
    //val whitespaceParser = zeroOrMore(whitespace)
    val commentParser = lineComment or blockComment
    val ignoredParser = zeroOrMore(whitespace or commentParser)
    val stringParser = stringLiteral.map { PropertyValue.StringValue(it.text.removeSurrounding("\"")) }
    val integerParser = integerLiteral.map { PropertyValue.IntValue(it.text.toInt()) }
    val floatParser = floatLiteral.map { PropertyValue.FloatValue(it.text.toFloat()) }
    val propertyValue = floatParser or integerParser or stringParser
    val property by (ignoredParser and identifier and ignoredParser and colon and ignoredParser and propertyValue).map { (_, id, _, _, _, value) ->
        id.text to value
    }
    val elementContent: Parser<List<Any>> = zeroOrMore(property or parser { element })
    val element: Parser<Any> by ignoredParser and identifier and ignoredParser and lBrace and elementContent and ignoredParser and rBrace

    override val tokens: List<Token> = listOf(
        identifier, lBrace, rBrace, colon, stringLiteral, floatLiteral, integerLiteral,
        whitespace, lineComment, blockComment
    )
    override val rootParser: Parser<List<Any>> = (oneOrMore(element) and ignoredParser).map { (elements, _) -> elements }
}

fun deserializeApp(parsedResult: List<Any>): App {
    val app = App()

    parsedResult.forEach { tuple ->
        when (tuple) {
            is Tuple7<*, *, *, *, *, *, *> -> {
                val elementName = (tuple.t2 as? TokenMatch)?.text
                val properties = extractProperties(tuple)

                when (elementName) {
                    "App" -> {
                        app.id = (properties["id"] as? PropertyValue.StringValue)?.value ?: ""
                        app.icon = (properties["icon"] as? PropertyValue.StringValue)?.value ?: ""
                        app.name = (properties["name"] as? PropertyValue.StringValue)?.value ?: ""
                        app.smlVersion = (properties["smlVersion"] as? PropertyValue.StringValue)?.value ?: ""
                        parseNestedAppElements(extractChildElements(tuple), app)
                    }
                }
            }
        }
    }
    return app
}

fun extractProperties(element: Any): Map<String, PropertyValue> {
    if (element is Tuple7<*, *, *, *, *, *, *>) {
        return (element.t5 as? List<*>)?.filterIsInstance<Pair<String, PropertyValue>>()?.toMap() ?: emptyMap()
    }
    return emptyMap()
}

fun extractChildElements(element: Any): List<Any> {
    if (element is Tuple7<*, *, *, *, *, *, *>) {
        return (element.t5 as? List<*>)?.filterIsInstance<Tuple7<*, *, *, *, *, *, *>>() ?: emptyList()
    }
    return emptyList()
}

fun deserializePage(parsedResult: List<Any>): Page {
    val page = Page(color = "", backgroundColor = "", padding = Padding(0, 0, 0, 0), elements = mutableListOf())

    parsedResult.forEach { tuple ->
        when (tuple) {
            is Tuple7<*, *, *, *, *, *, *> -> {
                val elementName = (tuple.t2 as? TokenMatch)?.text
                val properties = extractProperties(tuple)

                when (elementName) {
                    "Page" -> {
                        page.color = (properties["color"] as? PropertyValue.StringValue)?.value ?: ""
                        page.backgroundColor = (properties["backgroundColor"] as? PropertyValue.StringValue)?.value ?: ""
                        page.padding = parsePadding((properties["padding"] as? PropertyValue.StringValue)?.value ?: "0")
                        parseNestedElements(extractChildElements(tuple), page.elements as MutableList<UIElement>)
                    }
                }
            }
        }
    }

    return page
}

fun parseNestedElements(nestedElements: List<Any>, elements: MutableList<UIElement>) {
    nestedElements.forEach { element ->
        when (element) {
            is Tuple7<*, *, *, *, *, *, *> -> {
                val elementName = (element.t2 as? TokenMatch)?.text
                val properties = extractProperties(element)

                when (elementName) {
                    "Text" -> {
                        elements.add(
                            TextElement(
                            text = (properties["text"] as? PropertyValue.StringValue)?.value ?: "def",
                            color = hexToColor((properties["color"] as? PropertyValue.StringValue)?.value ?: ""),
                            fontSize = ((properties["fontSize"] as? PropertyValue.IntValue)?.value ?: 14).sp,
                            fontWeight = when((properties["fontWeight"] as? PropertyValue.StringValue)?.value ?: "") {
                                "bold" -> { FontWeight.Bold }
                                "black" -> { FontWeight.Black }
                                "thin" -> { FontWeight.Thin }
                                "extrabold" -> { FontWeight.ExtraBold }
                                "extralight" -> { FontWeight.ExtraLight }
                                "light" -> { FontWeight.Light }
                                "medium" -> { FontWeight.Medium }
                                "semibold" -> { FontWeight.SemiBold }
                                else -> { FontWeight.Normal }
                            },
                            textAlign = when((properties["textAlign"] as? PropertyValue.StringValue)?.value ?: "") {
                                "left" -> { TextAlign.Start }
                                "center" -> { TextAlign.Center }
                                "right" -> { TextAlign.End }
                                else -> { TextAlign.Unspecified }
                            })
                        )
                    }
                    "Column" -> {
                        val col = ColumnElement(padding = parsePadding((properties["padding"] as? PropertyValue.StringValue)?.value ?: "0"))
                        parseNestedElements(extractChildElements(element), col.uiElements as MutableList<UIElement>)
                        elements.add(col)
                    }
                    "Row" -> {
                        val row = RowElement(padding = parsePadding((properties["padding"] as? PropertyValue.StringValue)?.value ?: "0"))
                        parseNestedElements(extractChildElements(element), row.uiElements as MutableList<UIElement>)
                        elements.add(row)
                    }
                    "Markdown" -> {
                        val md = ((properties["text"] as? PropertyValue.StringValue)?.value ?: "").split("\n").joinToString("\n") { it.trim() }
                        val ele = MarkdownElement(text = md, color = (properties["color"] as? PropertyValue.StringValue)?.value ?: "#FFFFFF")
                        elements.add(ele)
                    }
                    "Button" -> {
                        val btn = ButtonElement(
                            label = (properties["label"] as? PropertyValue.StringValue)?.value ?: "",
                            link = (properties["link"] as? PropertyValue.StringValue)?.value ?: ""
                        )
                        elements.add(btn)
                    }
                    "Sound" -> {
                        val snd = SoundElement(src = (properties["src"] as? PropertyValue.StringValue)?.value ?: "")
                        elements.add(snd)
                    }
                    "Image" -> {
                        val img = ImageElement(
                            src = (properties["src"] as? PropertyValue.StringValue)?.value ?: "",
                            scale = (properties["scale"] as? PropertyValue.StringValue)?.value ?: "1",
                            link = (properties["link"] as? PropertyValue.StringValue)?.value ?: ""
                        )
                        elements.add(img)
                    }
                    "Spacer" -> {
                        val sp = SpacerElement(height = (properties["height"] as? PropertyValue.IntValue)?.value ?: 0)
                        elements.add(sp)
                    }
                    "Video" -> {
                        val vid = VideoElement(
                            src = (properties["src"] as? PropertyValue.StringValue)?.value ?: "",
                            height = (properties["height"] as? PropertyValue.IntValue)?.value ?: 100
                        )
                        elements.add(vid)
                    }
                    "Youtube" -> {
                        val yt = YoutubeElement(
                            id = (properties["id"] as? PropertyValue.StringValue)?.value ?: "",
                            height = (properties["height"] as? PropertyValue.IntValue)?.value ?: 100
                        )
                        elements.add(yt)
                    }
                }
            }
        }
    }
}

fun parseNestedAppElements(nestedElements: List<Any>, app: App) {
    nestedElements.forEach { element ->
        when (element) {
            is Tuple7<*, *, *, *, *, *, *> -> {
                val elementName = (element.t2 as? TokenMatch)?.text
                val properties = extractProperties(element)

                when (elementName) {
                    "Navigation" -> {
                        val type = (properties["type"] as? PropertyValue.StringValue)?.value ?: ""
                        app.navigation.type = type
                        parseNestedNavElements(extractChildElements(element), app.navigation)
                    }
                    "Deployment" -> {
                        parseNestedDeployElements(extractChildElements(element), app.deployment)
                    }
                }
            }
        }
    }
}

fun parseNestedNavElements(nestedElements: List<Any>, navigation: NavigationElement) {
    nestedElements.forEach { element ->
        when (element) {
            is Tuple7<*, *, *, *, *, *, *> -> {
                val elementName = (element.t2 as? TokenMatch)?.text
                val properties = extractProperties(element)

                when (elementName) {
                    "Item" -> {
                        val page = (properties["page"] as? PropertyValue.StringValue)?.value ?: ""
                        navigation.items.add(ItemElement(page))
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun parseNestedDeployElements(nestedElements: List<Any>, deployment: DeploymentElement) {
    nestedElements.forEach { element ->
        when (element) {
            is Tuple7<*, *, *, *, *, *, *> -> {
                val elementName = (element.t2 as? TokenMatch)?.text
                val properties = extractProperties(element)

                when (elementName) {
                    "File" -> {
                        val path = (properties["path"] as? PropertyValue.StringValue)?.value ?: ""
                        val date = (properties["time"] as? PropertyValue.StringValue)?.value ?: ""
                        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH.mm.ss")
                        val dateTime = LocalDateTime.parse(date, formatter)
                        deployment.files.add(FileElement(path, dateTime))
                    }
                }
            }
        }
    }
}

fun parsePage(sml: String): Page? {
    try {
        val result = SmlGrammar.parseToEnd(sml)
        return deserializePage(result)
    } catch (e: Exception) {
        println("An error occurred while parsing a page: ${e.message}\nSML: $sml")
        return null
    }
}

fun parseApp(sml: String): App? {
    try {
        val result = SmlGrammar.parseToEnd(sml)
        return deserializeApp(result)
    } catch (e: Exception) {
        println("An error occurred while parsing an app: ${e.message}\nSML: $sml")
        return null
    }
}
