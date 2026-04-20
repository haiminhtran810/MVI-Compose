package tmh.learn.weathercompose.domain.repository

import tmh.learn.weathercompose.domain.entity.Location

interface LocationRepository {
    suspend fun searchLocation(query: String): Result<List<Location>>
    suspend fun getCurrentDeviceLocation(): Result<Location>
    suspend fun getSavedLocations(): Result<List<Location>>
    suspend fun saveLocation(location: Location)
    suspend fun removeLocation(location: Location)
}
