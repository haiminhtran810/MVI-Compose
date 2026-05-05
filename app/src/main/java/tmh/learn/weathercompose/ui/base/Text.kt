package tmh.learn.weathercompose.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import tmh.learn.weathercompose.ui.theme.WeatherComposeTheme

@Composable
fun AppText(modifier: Modifier = Modifier, text: String) {
    Box(modifier = modifier) {
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun TextPreview() {
    WeatherComposeTheme {
        AppText(modifier = Modifier, text = "Sample Text")
    }
}