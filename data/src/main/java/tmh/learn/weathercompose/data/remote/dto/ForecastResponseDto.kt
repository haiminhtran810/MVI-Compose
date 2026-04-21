package tmh.learn.weathercompose.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForecastResponseDto(
    @SerializedName("list") val list: List<ForecastItemDto>
)

data class ForecastItemDto(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: MainDto,
    @SerializedName("weather") val weather: List<WeatherDto>,
    @SerializedName("wind") val wind: WindDto
)
