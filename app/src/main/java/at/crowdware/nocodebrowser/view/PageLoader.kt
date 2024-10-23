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
import android.view.Choreographer
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import at.crowdware.nocodebrowser.MainActivity
import at.crowdware.nocodebrowser.ui.Padding
import at.crowdware.nocodebrowser.ui.Page
import at.crowdware.nocodebrowser.ui.UIElement
import at.crowdware.nocodebrowser.ui.hexToColor
import at.crowdware.nocodebrowser.ui.parseMarkdown
import com.google.android.filament.utils.KTX1Loader
import com.google.android.filament.utils.ModelViewer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoadPage(
    name: String,
    navhostBackground: MutableState<Color>,
    mainActivity: MainActivity,
    navController: NavHostController
) {
    var page:Page? by remember { mutableStateOf(Page(color="#FFFFFF", backgroundColor = "#000000", padding = Padding(0,0,0,0), "false", elements = mutableListOf()))}
    var isLoading by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

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
            navhostBackground.value = hexToColor(bgColor, MaterialTheme.colorScheme.background)
            var modifier = Modifier as Modifier
            if (page!!.scrollable == "true") {
                modifier = modifier.verticalScroll(scrollState)
            }
            Column(
                modifier = modifier
                    .padding(
                        start = padding.left.dp,
                        top = padding.top.dp,
                        bottom = padding.bottom.dp,
                        end = padding.right.dp
                    )
                    .fillMaxSize()
                    .background(color = hexToColor(bgColor, MaterialTheme.colorScheme.background))
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
            renderButton(          modifier = if(element.weight > 0) Modifier.weight(element.weight.toFloat()) else Modifier, mainActivity = mainActivity, navController = navController, element = element)
        }
        is UIElement.ImageElement -> {
            dynamicImageFromAssets(modifier = if(element.weight > 0) Modifier.weight(element.weight.toFloat()) else Modifier, mainActivity = mainActivity, navcontroller = navController, filename = element.src, scale = element.scale, link = element.link)
        }
        is UIElement.VideoElement -> {
            dynamicVideofromAssets(modifier = if(element.weight > 0) Modifier.weight(element.weight.toFloat()) else Modifier, mainActivity = mainActivity, filename = element.src)
        }
        is UIElement.SoundElement -> {
            dynamicSoundfromAssets(mainActivity, element.src)
        }
        is UIElement.YoutubeElement -> {
            dynamicYoutube(modifier = if(element.weight > 0){Modifier.weight(element.weight.toFloat())} else {Modifier}, videoId = element.id)
        }
        is UIElement.SceneElement -> {
            dynamicScene(modifier = if(element.weight > 0){Modifier.weight(element.weight.toFloat())} else {Modifier}, element)
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
            renderButton(modifier= Modifier, element = element, mainActivity = mainActivity, navController = navController)
        }
        is UIElement.ImageElement -> {
            dynamicImageFromAssets(modifier = if(element.weight > 0){Modifier.weight(element.weight.toFloat())} else {Modifier}, mainActivity = mainActivity, navcontroller = navController, filename = element.src, scale =element.scale, link = element.link)
        }
        is UIElement.VideoElement -> {
            dynamicVideofromAssets(modifier = if(element.weight > 0){Modifier.weight(element.weight.toFloat())} else {Modifier}, mainActivity = mainActivity, filename = element.src)
        }
        is UIElement.SoundElement -> {
            dynamicSoundfromAssets(mainActivity, element.src)
        }
        is UIElement.YoutubeElement -> {
            dynamicYoutube(modifier = if(element.weight > 0){Modifier.weight(element.weight.toFloat())} else {Modifier}, videoId = element.id)
        }
        is UIElement.SceneElement -> {
            dynamicScene(modifier = if(element.weight > 0){Modifier.weight(element.weight.toFloat())} else {Modifier}, element)
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
    Row (horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(
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
        style = TextStyle(
            color = hexToColor(element.color, MaterialTheme.colorScheme.onBackground),
            fontSize = element.fontSize,
            fontWeight = element.fontWeight,
            textAlign = element.textAlign
        )
    )
}

@Composable
fun renderMarkdown(element: UIElement.MarkdownElement) {
    val parsedMarkdown = parseMarkdown(element.text.trim())
    Text(
        text = parsedMarkdown,
        style = TextStyle(
            color = hexToColor(element.color, MaterialTheme.colorScheme.onBackground),
            fontSize = element.fontSize,
            fontWeight = element.fontWeight,
            textAlign = element.textAlign
        )
    )
}

@Composable
fun renderButton(
    modifier: Modifier = Modifier,
    element: UIElement.ButtonElement,
    mainActivity: MainActivity,
    navController: NavHostController
) {
    var colors = buttonColors()

    if(element.color.isNotEmpty() && element.backgroundColor.isNotEmpty())
        colors = buttonColors(
            containerColor = hexToColor(element.backgroundColor, Color.Unspecified),
            contentColor = hexToColor(element.color, Color.Unspecified))
    else if(element.color.isNotEmpty())
        colors = buttonColors(contentColor = hexToColor(element.color, Color.Unspecified))
    else if(element.backgroundColor.isNotEmpty())
        colors = buttonColors(
            containerColor = hexToColor(element.backgroundColor, Color.Unspecified))

    Button(
        modifier = modifier
            .fillMaxWidth()
            .then(if(element.width > 0)Modifier.width(element.width.dp)else Modifier)
            .then(if(element.height > 0)Modifier.height(element.height.dp)else Modifier),
        colors = colors,
        onClick =  { handleButtonClick(element.link, mainActivity, navController) }) {
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
            renderButton(modifier = Modifier, element = element, mainActivity = mainActivity, navController= navController)
        }
        is UIElement.ImageElement -> {
            dynamicImageFromAssets(modifier = Modifier, mainActivity, navcontroller = navController, filename = element.src, scale = element.scale, link= element.link)
        }
        is UIElement.VideoElement -> {
            dynamicVideofromAssets(modifier= Modifier, mainActivity = mainActivity,element.src)
        }
        is UIElement.SoundElement -> {
            dynamicSoundfromAssets(mainActivity, element.src)
        }
        is UIElement.YoutubeElement -> {
            dynamicYoutube(modifier = Modifier, videoId = element.id)
        }
        is UIElement.SceneElement -> {
            dynamicScene(modifier = Modifier, element = element)
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
        link.startsWith("animation:") -> {
            val aniType = link.removePrefix("animation:")
            mainActivity.sendToAnimation(aniType)
        }
        else -> {
            println("Unknown link type: $link")
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun dynamicImageFromAssets(modifier: Modifier = Modifier, mainActivity: MainActivity, navcontroller: NavController, filename: String, scale: String, link: String) {
    var cacheName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        cacheName = withContext(Dispatchers.IO) {
            mainActivity.contentLoader.loadAsset(filename, "images")
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
                modifier = modifier.fillMaxWidth()
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
            mainActivity.contentLoader.loadAsset(filename ,"sounds")
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
fun dynamicVideofromAssets(modifier: Modifier = Modifier, mainActivity: MainActivity, filename: String, height: Int = 200) {
    var cacheName by remember { mutableStateOf("") }
    if(filename.startsWith("http")) {
        cacheName = filename
    } else {
        LaunchedEffect(Unit) {
            cacheName = withContext(Dispatchers.IO) {
                mainActivity.contentLoader.loadAsset(filename, "videos")
            }
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
            modifier = modifier.fillMaxWidth().height(height.dp)
        )
    }
}

@Composable
fun dynamicYoutube(modifier: Modifier = Modifier, videoId: String, height: Int = 200) {
    val ctx = LocalContext.current

    AndroidView(
        modifier = modifier.height(height.dp),
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
        }
    )
}

fun loadBitmapFromAssets(context: Context, filename: String): Bitmap? {
    return try {
        val file = File(context.filesDir, filename)
        if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

/*
@Composable
fun dynamicScene(modifier: Modifier = Modifier, element: UIElement.SceneElement) {
    val ctx = LocalContext.current as MainActivity

    AndroidView(
        factory = { context ->
            SurfaceView(context).apply {
                ctx.modelViewer = ModelViewer(this)
                setOnTouchListener(ctx.modelViewer)
                ctx.loadGltf(element.gltf)
                ctx.loadIbl(element.ibl)
                ctx.loadSkybox(element.skybox)
                //onModelLoaded()
                ctx.choreographer.postFrameCallback(ctx.frameCallback)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .then(if(element.width>0)Modifier.width(element.width.dp)else Modifier)
            .then(if(element.height>0)Modifier.height(element.height.dp)else Modifier)
    )

    DisposableEffect(Unit) {
        onDispose {
            // Remove the frame callback when the composable is disposed
            ctx.choreographer.removeFrameCallback(ctx.frameCallback)
        }
    }
}
*/

@Composable
fun dynamicScene(modifier: Modifier = Modifier, element: UIElement.SceneElement) {
    val context = LocalContext.current
    val surfaceView = remember { SurfaceView(context) }
    val modelViewer = remember(element.model, element.ibl, element.skybox) {
        ModelViewer(surfaceView).apply {
            try {
                val buffer = readAsset("models/${element.model}", context)
                if (element.model.endsWith(".gltf"))
                    loadModelGltf(buffer) { uri -> readAsset("$uri", context) }
                else
                    loadModelGlb(buffer)
                transformToUnitCube()

                val bufferIbl = readAsset("models/${element.ibl}", context)
                KTX1Loader.createIndirectLight(engine, bufferIbl).apply {
                    intensity = 50_000f
                    scene.indirectLight = this
                }

                val bufferSkybox = readAsset("models/${element.skybox}", context)
                KTX1Loader.createSkybox(engine, bufferSkybox).apply {
                    scene.skybox = this
                }

                surfaceView.setOnTouchListener(this)
            } catch(e: Exception) {
                println("Error in dynamic scene: ${e.message}")
            }
        }
    }
    AndroidView(
        factory = { surfaceView },
        modifier = modifier
            .fillMaxWidth()
            .then(if (element.width > 0) Modifier.width(element.width.dp) else Modifier)
            .then(if (element.height > 0) Modifier.height(element.height.dp) else Modifier)
    )
    val choreographer = remember { Choreographer.getInstance() }
    val startTime = remember { System.nanoTime() }
    val frameCallback = remember {
        object : Choreographer.FrameCallback {
            override fun doFrame(currentTime: Long) {
                // Berechne die vergangene Zeit in Sekunden
                val seconds = (currentTime - startTime).toDouble() / 1_000_000_000
                choreographer.postFrameCallback(this)

                // Handle animation and rendering logic
                modelViewer.animator?.apply {
                    if (animationCount > 0) {
                        applyAnimation(0, seconds.toFloat())
                    }
                    updateBoneMatrices()
                }
                modelViewer.render(currentTime)
            }
        }
    }

    DisposableEffect(Unit) {
        choreographer.postFrameCallback(frameCallback)
        onDispose {
            choreographer.removeFrameCallback(frameCallback)
        }
    }
}

fun readAsset(assetName: String, context: Context): ByteBuffer {
    val input = context.assets.open(assetName)
    val bytes = ByteArray(input.available())
    input.read(bytes)
    return ByteBuffer.wrap(bytes)
}
