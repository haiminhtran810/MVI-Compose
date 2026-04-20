package tmh.learn.weathercompose.domain.usecase

import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.repository.LocationRepository

class GetSavedLocationsUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Result<List<Location>> {
        return locationRepository.getSavedLocations()
    }
}
