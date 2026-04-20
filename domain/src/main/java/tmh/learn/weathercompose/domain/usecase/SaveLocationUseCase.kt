package tmh.learn.weathercompose.domain.usecase

import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.repository.LocationRepository

class SaveLocationUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(location: Location) {
        locationRepository.saveLocation(location)
    }
}
