package tmh.learn.weathercompose

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import tmh.learn.weathercompose.data.di.dataModule
import tmh.learn.weathercompose.di.appModule
import tmh.learn.weathercompose.domain.di.domainModule

class WeatherComposeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WeatherComposeApp)
            modules(dataModule, domainModule, appModule)
        }
    }
}
