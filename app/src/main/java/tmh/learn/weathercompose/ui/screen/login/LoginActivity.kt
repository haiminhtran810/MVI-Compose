package tmh.learn.weathercompose.ui.screen.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import tmh.learn.weathercompose.ui.screen.BaseActivity
import tmh.learn.weathercompose.ui.screen.main.WeatherRoute
import tmh.learn.weathercompose.ui.theme.WeatherComposeTheme

class LoginActivity : BaseActivity() {
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