package tmh.learn.weathercompose.data.repository

import tmh.learn.weathercompose.data.BuildConfig
import tmh.learn.weathercompose.data.mapper.toDomain
import tmh.learn.weathercompose.data.remote.OpenWeatherApi
import tmh.learn.weathercompose.domain.entity.Forecast
import tmh.learn.weathercompose.domain.entity.Weather
import tmh.learn.weathercompose.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val api: OpenWeatherApi
) : WeatherRepository {

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<Weather> {
        return try {
            val response = api.getCurrentWeather(
                lat = latitude,
                lon = longitude,
                apiKey = BuildConfig.OPENWEATHER_API_KEY
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getForecast(latitude: Double, longitude: Double): Result<Forecast> {
        return try {
            val response = api.getForecast(
                lat = latitude,
                lon = longitude,
                apiKey = BuildConfig.OPENWEATHER_API_KEY
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
