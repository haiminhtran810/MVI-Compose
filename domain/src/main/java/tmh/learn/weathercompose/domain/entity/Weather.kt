package tmh.learn.weathercompose.domain.entity

data class Weather(
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val iconId: String,
    val humidity: Int,
    val windSpeed: Double,
    val conditionCode: Int
)
