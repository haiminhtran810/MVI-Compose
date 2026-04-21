package tmh.learn.weathercompose.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponseDto(
    @SerializedName("weather") val weather: List<WeatherDto>,
    @SerializedName("main") val main: MainDto,
    @SerializedName("wind") val wind: WindDto
)

data class WeatherDto(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class MainDto(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("humidity") val humidity: Int
)

data class WindDto(
    @SerializedName("speed") val speed: Double
)
