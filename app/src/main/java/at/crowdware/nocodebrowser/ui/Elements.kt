/*
 * Copyright (C) 2024 CrowdWare
 *
 * This file is part of NoCodeLib.
 *
 *  NoCodeLib is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  NoCodeLib is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with NoCodeLib.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.crowdware.nocodebrowser.ui
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

data class App(
    var name: String = "",
    var icon: String = "",
    var id: String = "",
    var smlVersion: String = "",
    val navigation: NavigationElement = NavigationElement(),
    val deployment: DeploymentElement = DeploymentElement()
)

data class NavigationElement(
    var type: String = "",
    val items: MutableList<ItemElement> = mutableListOf()
)

data class DeploymentElement(
    val files: MutableList<FileElement> = mutableListOf()
)

data class FileElement(val path: String, val time: String)

data class ItemElement (val page: String)


data class Page(
    var color: String,
    var backgroundColor: String,
    var padding: Padding,
    val elements: MutableList<UIElement>)

sealed class UIElement {
    data object Zero : UIElement()
    data class TextElement(
        val text: String,
        val color: Color,
        val fontSize: TextUnit,
        val fontWeight: FontWeight,
        val textAlign: TextAlign
    ) : UIElement()
    data class ButtonElement(
        val label: String,
        val link: String) : UIElement()
    data class ImageElement(
        val src: String,
        val scale: String,
        val link: String) : UIElement()
    data class SpacerElement(
        val height: Int) : UIElement()
    data class VideoElement(
        val src: String,
    val height: Int) : UIElement()
    data class YoutubeElement(
        val id: String,
        val height: Int) : UIElement()
    data class SoundElement(
        val src: String) : UIElement()
    data class RowElement(
        val padding: Padding,
        val uiElements: MutableList<UIElement> = mutableListOf()) : UIElement()
    data class ColumnElement(
        val padding: Padding,
        val uiElements: MutableList<UIElement> = mutableListOf()) : UIElement()
    data class MarkdownElement(
        val text: String,
        val color: String) : UIElement()
}

data class Padding(val top: Int, val right: Int, val bottom: Int, val left: Int)
