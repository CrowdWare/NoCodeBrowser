package at.crowdware.nocodebrowser.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import at.crowdware.nocodebrowser.ui.theme.NoCodeBrowserTheme

@Composable
fun Home(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    NoCodeBrowserTheme {
        Home("Android")
    }
}