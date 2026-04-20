package tmh.learn.weathercompose.domain.usecase

import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.repository.LocationRepository

class GetDeviceLocationUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Result<Location> {
        return locationRepository.getCurrentDeviceLocation()
    }
}
