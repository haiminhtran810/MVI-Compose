package tmh.learn.weathercompose.ui.screen.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import tmh.learn.weathercompose.ui.screen.BaseActivity
import tmh.learn.weathercompose.ui.theme.WeatherComposeTheme

class WeatherActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherComposeTheme {
                WeatherRoute()
            }
        }
    }
}