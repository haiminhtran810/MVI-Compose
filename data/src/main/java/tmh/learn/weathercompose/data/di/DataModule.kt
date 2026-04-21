package tmh.learn.weathercompose.data.di

import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tmh.learn.weathercompose.data.BuildConfig
import tmh.learn.weathercompose.data.remote.OpenWeatherApi
import tmh.learn.weathercompose.data.repository.LocationRepositoryImpl
import tmh.learn.weathercompose.data.repository.WeatherRepositoryImpl
import tmh.learn.weathercompose.domain.repository.LocationRepository
import tmh.learn.weathercompose.domain.repository.WeatherRepository

private const val OPEN_WEATHER_BASE_URL = "https://api.openweathermap.org/"

val dataModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(OPEN_WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create(OpenWeatherApi::class.java)
    }

    single {
        LocationServices.getFusedLocationProviderClient(androidContext())
    }

    single<WeatherRepository> {
        WeatherRepositoryImpl(api = get())
    }

    single<LocationRepository> {
        LocationRepositoryImpl(
            api = get(),
            fusedLocationProviderClient = get(),
            appContext = androidContext()
        )
    }
}
