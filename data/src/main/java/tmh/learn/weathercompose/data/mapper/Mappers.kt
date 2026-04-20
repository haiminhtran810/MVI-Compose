package tmh.learn.weathercompose.data.mapper

import tmh.learn.weathercompose.data.remote.dto.ForecastItemDto
import tmh.learn.weathercompose.data.remote.dto.ForecastResponseDto
import tmh.learn.weathercompose.data.remote.dto.GeocodingResponseDto
import tmh.learn.weathercompose.data.remote.dto.WeatherResponseDto
import tmh.learn.weathercompose.domain.entity.DailyForecast
import tmh.learn.weathercompose.domain.entity.Forecast
import tmh.learn.weathercompose.domain.entity.HourlyForecast
import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.entity.Weather

fun WeatherResponseDto.toDomain(): Weather {
    val weatherInfo = weather.firstOrNull()
    return Weather(
        temperature = main.temp,
        feelsLike = main.feelsLike,
        description = weatherInfo?.description ?: "",
        iconId = weatherInfo?.icon ?: "",
        humidity = main.humidity,
        windSpeed = wind.speed,
        conditionCode = weatherInfo?.id ?: 0
    )
}

fun ForecastResponseDto.toDomain(): Forecast {
    val hourly = list.take(8).map {
        val weatherInfo = it.weather.firstOrNull()
        HourlyForecast(
            timeEpoch = it.dt,
            temperature = it.main.temp,
            iconId = weatherInfo?.icon ?: "",
            description = weatherInfo?.description ?: ""
        )
    }

    val daily = list.groupBy { it.dt / 86400 }.values.map { dayList ->
        val minTemp = dayList.minOf { it.main.temp }
        val maxTemp = dayList.maxOf { it.main.temp }
        val firstInDay = dayList.first()
        val weatherInfo = firstInDay.weather.firstOrNull()
        DailyForecast(
            dateEpoch = firstInDay.dt,
            minTemp = minTemp,
            maxTemp = maxTemp,
            iconId = weatherInfo?.icon ?: "",
            description = weatherInfo?.description ?: ""
        )
    }.take(5)

    return Forecast(
        hourly = hourly,
        daily = daily
    )
}

fun GeocodingResponseDto.toDomain(): Location {
    return Location(
        name = name,
        country = country ?: "",
        latitude = lat,
        longitude = lon
    )
}
