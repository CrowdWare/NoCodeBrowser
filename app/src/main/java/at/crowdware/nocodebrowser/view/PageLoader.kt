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
import androidx.core.content.contentValuesOf
import androidx.navigation.NavHostController
import at.crowdware.nocodebrowser.MainActivity
import at.crowdware.nocodebrowser.ui.*
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
fun RenderElement(
    element: UIElement,
    mainActivity: MainActivity,
    navController: NavHostController
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
                    RenderElement(ele, mainActivity, navController)
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
                    RenderElement(ele, mainActivity, navController)
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
            Button(modifier = Modifier.fillMaxWidth(), onClick =  { handleButtonClick(element.link, mainActivity, navController) }) {
                Text(text = element.label)
            }
        }
        is UIElement.ImageElement -> {
            dynamicImageFromAssets(mainActivity, navController, filename = element.src, element.scale, element.link)
        }
        is UIElement.VideoElement -> {
            dynamicVideofromAssets(mainActivity, navController,element.src, element.height)
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
fun dynamicImageFromAssets( mainActivity: MainActivity, navController: NavHostController, filename: String, scale: String, link: String) {
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

@Composable
fun dynamicSoundfromAssets(filename: String) {
    // TODO: load Sound from webserver
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun dynamicVideofromAssets(mainActivity: MainActivity, navController: NavHostController, filename: String, height: Int) {
    var cacheName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        cacheName = withContext(Dispatchers.IO) {
            mainActivity.contentLoader.loadAsset(filename)
        }
    }
   /*
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    val mediaItem = remember(videoUri) {
        if (videoUri.startsWith("http")) {
            MediaItem.fromUri(Uri.parse(videoUri))
        } else {
            MediaItem.fromUri(Uri.parse("asset:///$videoUri"))
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
            .height(element.height.dp)
    )

    */
}

@Composable
fun dynamicYoutube(height: Int) {
    // TODO: load Youtube
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