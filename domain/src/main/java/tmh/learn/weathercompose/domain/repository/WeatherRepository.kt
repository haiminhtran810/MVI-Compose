package tmh.learn.weathercompose.domain.repository

import tmh.learn.weathercompose.domain.entity.Forecast
import tmh.learn.weathercompose.domain.entity.Weather

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<Weather>
    suspend fun getForecast(latitude: Double, longitude: Double): Result<Forecast>
}
