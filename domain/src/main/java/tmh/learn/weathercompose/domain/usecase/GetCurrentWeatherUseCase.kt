package tmh.learn.weathercompose.domain.usecase

import tmh.learn.weathercompose.domain.entity.Weather
import tmh.learn.weathercompose.domain.repository.WeatherRepository

class GetCurrentWeatherUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<Weather> {
        return weatherRepository.getCurrentWeather(latitude, longitude)
    }
}
