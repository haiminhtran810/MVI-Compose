package tmh.learn.weathercompose.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import tmh.learn.weathercompose.data.remote.dto.ForecastResponseDto
import tmh.learn.weathercompose.data.remote.dto.GeocodingResponseDto
import tmh.learn.weathercompose.data.remote.dto.WeatherResponseDto

interface OpenWeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): WeatherResponseDto

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): ForecastResponseDto

    @GET("geo/1.0/direct")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): List<GeocodingResponseDto>
}
