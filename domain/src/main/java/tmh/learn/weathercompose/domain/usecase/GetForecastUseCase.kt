package tmh.learn.weathercompose.domain.usecase

import tmh.learn.weathercompose.domain.entity.Forecast
import tmh.learn.weathercompose.domain.repository.WeatherRepository

class GetForecastUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<Forecast> {
        return weatherRepository.getForecast(latitude, longitude)
    }
}
