package tmh.learn.weathercompose.domain.usecase

import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.repository.LocationRepository

class SearchLocationUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(query: String): Result<List<Location>> {
        return locationRepository.searchLocation(query)
    }
}
