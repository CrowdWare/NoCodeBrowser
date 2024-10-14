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
package at.crowdware.nocodebrowser.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.contentValuesOf
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import at.crowdware.nocodebrowser.MainActivity
import at.crowdware.nocodebrowser.ui.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoadPage(
    name: String,
    navhostBackground: MutableState<Color>,
    mainActivity: MainActivity,
    navController: NavHostController
) {
    //val context = LocalContext.current
    var page:Page? by remember { mutableStateOf(Page(color="#FFFFFF", backgroundColor = "#000000", padding = Padding(0,0,0,0), elements = mutableListOf()))}
    var isLoading by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        page = withContext(Dispatchers.IO) {
            mainActivity.contentLoader.loadPage(name)
        }
        isLoading = false
    }
    if (isLoading) {
        Box (modifier = Modifier.fillMaxSize().background(color = navhostBackground.value)){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        if (page != null) {
            val padding = page!!.padding
            val bgColor = page!!.backgroundColor
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
                RenderPage(page!!, mainActivity, navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RenderPage(
    page: Page,
    mainActivity: MainActivity,
    navController: NavHostController
) {
    for (element in page.elements) {
        RenderElement(element, mainActivity, navController)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RowScope.RenderElement(mainActivity: MainActivity, navController: NavHostController, element: UIElement) {
    when(element) {
        is UIElement.ColumnElement -> {
            renderColumn(mainActivity, navController, element)
        }
        is UIElement.RowElement -> {
            renderRow(mainActivity, navController, element)
        }
        is UIElement.TextElement -> {
            renderText(element)
        }
        is UIElement.MarkdownElement -> {
            renderMarkdown(element)
        }
        is UIElement.ButtonElement -> {
            renderButton(mainActivity, navController, element)
        }
        is UIElement.ImageElement -> {
            dynamicImageFromAssets(mainActivity, navController, element.src, element.scale, element.link)
        }
        is UIElement.VideoElement -> {
            dynamicVideofromAssets(mainActivity, element.src)
        }
        is UIElement.SoundElement -> {
            dynamicSoundfromAssets(mainActivity, element.src)
        }
        is UIElement.YoutubeElement -> {
            dynamicYoutube(element.id)
        }
        is UIElement.SpacerElement -> {
            var mod = Modifier as Modifier
            if (element.amount > 0)
                mod = mod.then(Modifier.width(element.amount.dp))
            if (element.weight > 0)
                mod = mod.then(Modifier.weight(element.weight.toFloat()))
            Spacer(modifier = mod)
        }
        else -> {}
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ColumnScope.RenderElement(mainActivity: MainActivity, navController: NavHostController, element: UIElement) {
    when (element) {
        is UIElement.ColumnElement -> {
            renderColumn(mainActivity, navController, element)
        }
        is UIElement.RowElement -> {
            renderRow(mainActivity, navController, element)
        }
        is UIElement.TextElement -> {
            renderText(element)
        }
        is UIElement.MarkdownElement -> {
            renderMarkdown(element)
        }
        is UIElement.ButtonElement -> {
            renderButton(mainActivity, navController, element)
        }
        is UIElement.ImageElement -> {
            dynamicImageFromAssets(mainActivity, navController, filename = element.src, element.scale, element.link)
        }
        is UIElement.VideoElement -> {
            dynamicVideofromAssets(mainActivity, element.src)
        }
        is UIElement.SoundElement -> {
            dynamicSoundfromAssets(mainActivity, element.src)
        }
        is UIElement.YoutubeElement -> {
            dynamicYoutube(element.id)
        }
        is UIElement.SpacerElement -> {
            var mod = Modifier as Modifier
            if (element.amount > 0)
                mod = mod.then(Modifier.height(element.amount.dp))
            if (element.weight > 0)
                mod = mod.then(Modifier.weight(element.weight.toFloat()))
            Spacer(modifier = mod)
        }
        else -> {}
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun renderColumn(mainActivity: MainActivity, navcontroller: NavHostController, element: UIElement.ColumnElement) {
    Column (modifier = Modifier.padding(
        top = element.padding.top.dp,
        bottom = element.padding.bottom.dp,
        start = element.padding.left.dp,
        end = element.padding.right.dp
    )) {
        for (ele in element.uiElements) {
            RenderElement(mainActivity, navcontroller, ele)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun renderRow(mainActivity: MainActivity, navController: NavHostController, element: UIElement.RowElement) {
    Row (modifier = Modifier.padding(
        top = element.padding.top.dp,
        bottom = element.padding.bottom.dp,
        start = element.padding.left.dp,
        end = element.padding.right.dp
    )) {
        for (ele in element.uiElements) {
            RenderElement(mainActivity, navController, ele)
        }
    }
}

@Composable
fun renderText(element: UIElement.TextElement) {
    Text(
        text = element.text.trim(),
        fontSize = element.fontSize,
        style = TextStyle(color = element.color)
    )
}

@Composable
fun renderMarkdown(element: UIElement.MarkdownElement) {
    val parsedMarkdown = parseMarkdown(element.text.trim())
    Text(
        text = parsedMarkdown,
        style = TextStyle(color = hexToColor(element.color))
    )
}

@Composable
fun renderButton(mainActivity: MainActivity, navController: NavHostController, element: UIElement.ButtonElement) {
    Button(modifier = Modifier.fillMaxWidth(), onClick =  { handleButtonClick(element.link, mainActivity, navController) }) {
        Text(text = element.label)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RenderElement(
    element: UIElement,
    mainActivity: MainActivity,
    navController: NavHostController
) {
    when (element) {
        is UIElement.ColumnElement -> {
            renderColumn(mainActivity, navController, element)
        }
        is UIElement.RowElement -> {
            renderRow(mainActivity, navController, element)
        }
        is UIElement.TextElement -> {
            renderText(element)
        }
        is UIElement.MarkdownElement -> {
           renderMarkdown(element)
        }
        is UIElement.ButtonElement -> {
            renderButton(mainActivity, navController, element)
        }
        is UIElement.ImageElement -> {
            dynamicImageFromAssets(mainActivity, navController, filename = element.src, element.scale, element.link)
        }
        is UIElement.VideoElement -> {
            dynamicVideofromAssets(mainActivity,element.src)
        }
        is UIElement.SoundElement -> {
            dynamicSoundfromAssets(mainActivity, element.src)
        }
        is UIElement.YoutubeElement -> {
            dynamicYoutube(element.id)
        }
        else -> {}
    }
}

fun handleButtonClick(
    link: String,
    mainActivity: MainActivity,
    navController: NavHostController
) {
    when {
        link.startsWith("page:") -> {
            val pageId = link.removePrefix("page:")
            navController.navigate(pageId)
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun dynamicImageFromAssets( mainActivity: MainActivity, navcontroller: NavController, filename: String, scale: String, link: String) {
    var cacheName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        cacheName = withContext(Dispatchers.IO) {
            mainActivity.contentLoader.loadAsset(filename)
        }
    }
    if (cacheName.isNotEmpty()) {
        val bitmap = loadBitmapFromAssets(mainActivity, cacheName)
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = when(scale) {
                    "crop" -> ContentScale.Crop
                    "fit" -> ContentScale.Fit
                    "inside" -> ContentScale.Inside
                    "fillwidth" -> ContentScale.FillWidth
                    "fillbounds" -> ContentScale.FillBounds
                    "fillheight" -> ContentScale.FillHeight
                    "none" -> ContentScale.None
                    else -> ContentScale.Fit
                },
                modifier = Modifier.fillMaxWidth()
            )
      } else {
          Text(text = "Image [$filename] not found")
        }
    } else {
        Text(text = "Image [$filename] not loaded")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun dynamicSoundfromAssets(mainActivity: MainActivity, filename: String) {
    var cacheName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        cacheName = withContext(Dispatchers.IO) {
            mainActivity.contentLoader.loadAsset(filename)
        }
    }
    if (cacheName.isNotEmpty()) {
        val exoPlayer = remember { ExoPlayer.Builder(mainActivity).build() }
        val mediaItem = remember(filename) {
            if (filename.startsWith("http")) {
                MediaItem.fromUri(Uri.parse(cacheName))
            } else {
                val file = File(mainActivity.filesDir, cacheName)
                val uri = Uri.fromFile(file)
                MediaItem.fromUri(uri)
            }
        }
        LaunchedEffect(mediaItem) {
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
        exoPlayer.playWhenReady = true

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun dynamicVideofromAssets(mainActivity: MainActivity, filename: String) {
    var cacheName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        cacheName = withContext(Dispatchers.IO) {
            mainActivity.contentLoader.loadAsset(filename)
        }
    }
    if (cacheName.isNotEmpty()) {
        val exoPlayer = remember { ExoPlayer.Builder(mainActivity).build() }
        val mediaItem = remember(filename) {
            if (filename.startsWith("http")) {
                MediaItem.fromUri(Uri.parse(cacheName))
            } else {
                val file = File(mainActivity.filesDir, cacheName)
                val uri = Uri.fromFile(file)
                MediaItem.fromUri(uri)
            }
        }
        LaunchedEffect(mediaItem) {
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
        exoPlayer.playWhenReady = true

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun dynamicYoutube(videoId: String) {
    val ctx = LocalContext.current

    AndroidView(
        factory = {
            var view = YouTubePlayerView(it)
            val fragment = view.addYouTubePlayerListener(
                object : AbstractYouTubePlayerListener() {
                    override fun onReady(
                        youTubePlayer:
                        YouTubePlayer
                    ) {
                        super.onReady(youTubePlayer)
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                }
            )
            view
        })
}

fun loadBitmapFromAssets(context: Context, filename: String): Bitmap? {
    return try {
        // Get the file located in context.filesDir
        val file = File(context.filesDir, filename)

        // Check if the file exists
        if (file.exists()) {
            // Use BitmapFactory to decode the file into a Bitmap
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null // Return null if the file does not exist
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}