package at.crowdware.nocodebrowser.utils
import android.content.Context
import at.crowdware.nocodebrowser.parseApp
import at.crowdware.nocodebrowser.ui.App
import kotlinx.coroutines.*
import okhttp3.*
import java.io.File
import java.io.IOException

class ContentLoader {

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var context: Context

    // Initialize the OkHttp client and setup cache directory
    fun init(ctx: Context) {
        context = ctx
        okHttpClient = OkHttpClient.Builder().build()

        val directory = File(ctx.filesDir, "ContentCache")
        if (!directory.exists()) {
            directory.mkdir()
        }
    }

    // Suspend function to load the app asynchronously
    suspend fun loadApp(url: String): App? = withContext(Dispatchers.IO) {
        var fileContent = ""
        var app: App? = null

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
            println("Parsing the app")
            app = parseApp(appContent)
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