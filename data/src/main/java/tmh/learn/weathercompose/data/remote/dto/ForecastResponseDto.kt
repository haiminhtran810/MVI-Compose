package tmh.learn.weathercompose.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponseDto(
    @Json(name = "list") val list: List<ForecastItemDto>
)

@JsonClass(generateAdapter = true)
data class ForecastItemDto(
    @Json(name = "dt") val dt: Long,
    @Json(name = "main") val main: MainDto,
    @Json(name = "weather") val weather: List<WeatherDto>,
    @Json(name = "wind") val wind: WindDto
)
