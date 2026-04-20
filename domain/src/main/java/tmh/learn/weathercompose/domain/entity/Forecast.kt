package tmh.learn.weathercompose.domain.entity

data class Forecast(
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>
)

data class HourlyForecast(
    val timeEpoch: Long,
    val temperature: Double,
    val iconId: String,
    val description: String
)

data class DailyForecast(
    val dateEpoch: Long,
    val minTemp: Double,
    val maxTemp: Double,
    val iconId: String,
    val description: String
)
