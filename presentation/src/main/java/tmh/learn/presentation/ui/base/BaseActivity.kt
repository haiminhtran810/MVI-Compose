package tmh.learn.presentation.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity

@SuppressLint("RestrictedApi")
open class BaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}