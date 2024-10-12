package at.crowdware.nocodebrowser.utils
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import at.crowdware.nocodebrowser.parseApp
import at.crowdware.nocodebrowser.parsePage
import at.crowdware.nocodebrowser.ui.App
import at.crowdware.nocodebrowser.ui.Page
import kotlinx.coroutines.*
import okhttp3.*
import java.io.File
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ContentLoader {

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var context: Context
    lateinit var app: App
    private lateinit var appUrl: String
    private var appLoaded = false

    // Initialize the OkHttp client and setup cache directory
    fun init(ctx: Context) {
        context = ctx
        okHttpClient = OkHttpClient.Builder().build()

        val directory = File(ctx.filesDir, "ContentCache")
        if (!directory.exists()) {
            directory.mkdir()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadPage(name: String): Page? {
        var fileContent = ""
        val url = "$appUrl/pages/$name.sml"
        val result = app.deployment.files.find { it.path == "$name.sml" }
        if (result == null) {
            return null
        }
        val fileName = "ContentCache/" + appUrl.substringAfter("://") + "/pages/$name.sml"
        val file = File(context.filesDir, fileName)
        fileContent = if (file.exists()) {
            val lastModifiedMillis = file.lastModified()
            val lastModifiedDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModifiedMillis), ZoneId.systemDefault())
            if (result.time.isAfter(lastModifiedDateTime)) {
                // web server version is newer
                loadAndCacheSml(url, fileName, result.time) ?: ""
            } else {
                // use cache version instead
                file.readText()
            }
        } else {
            loadAndCacheSml(url, fileName, result.time) ?: ""
        }
        var page = parsePage(fileContent)
        if (page == null) {
            page = parsePage("Page { Column { padding: \"16\" Text { color: \"#FF0000\" fontSize: 18 text:\"An error occurred loading the home page.\"}}}")
        }
        return page
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loadAndCacheSml(url: String, fileName: String, time: LocalDateTime): String? {
        val sml = withContext(Dispatchers.IO) {
            downloadSml(url)
        }
        if (sml != null) {
            val cache = File(context.filesDir, fileName)
            // Make sure the parent directories exist
            val parentDir = cache.parentFile
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs()
            }
            cache.writeText(sml)
            val millis = time
                .atZone(ZoneId.systemDefault()) // Convert to ZonedDateTime in the system's default time zone
                .toInstant() // Convert to Instant (which represents a moment in time)
                .toEpochMilli()
            cache.setLastModified(millis)
        }
        return sml
    }

    // Suspend function to load the app asynchronously
    suspend fun loadApp(url: String): App? = withContext(Dispatchers.IO) {
        var fileContent = ""

        appUrl = url.substringBefore("/app.sml")

        val fileName = "ContentCache/" + url.substringAfter("://")
        val file = File(context.filesDir, fileName)

        // Make sure the parent directories exist
        val parentDir = file.parentFile
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs()
        }

        // Check if cached file exists
        if (file.exists()) {
            fileContent = file.readText()
        }

        // Download content from the URL
        var appContent = downloadSml(url)

        if (appContent != null) {
            if (fileContent != appContent) {
                // Write new content to the cache if it has changed
                file.writeText(appContent)
            }
        } else {
            // Use cached content if available
            if (fileContent.isNotEmpty()) {
                appContent = fileContent
            }
        }
        // Parse the app content
        if (appContent != null && appContent.isNotEmpty()) {
            app = parseApp(appContent)
            appLoaded = true
            println("app loaded")
        }
        return@withContext app
    }

    // Suspend function to download content asynchronously
    suspend fun downloadSml(url: String): String? = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        return@withContext try {
            // Use OkHttp's enqueue for asynchronous call
            val response = okHttpClient.newCall(request).await()
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

    // Extension function to convert OkHttp Call into a suspending function
    private suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isCancelled) return
                continuation.resumeWith(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (continuation.isCancelled) return
                continuation.resumeWith(Result.success(response))
            }
        })

        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (ex: Throwable) {
                // Handle cancellation exception
            }
        }
    }
}