package tmh.learn.weathercompose.data.repository

import tmh.learn.weathercompose.data.BuildConfig
import tmh.learn.weathercompose.data.mapper.toDomain
import tmh.learn.weathercompose.data.remote.OpenWeatherApi
import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val api: OpenWeatherApi
) : LocationRepository {

    override suspend fun searchLocation(query: String): Result<List<Location>> {
        return try {
            val response = api.searchLocation(
                query = query,
                apiKey = BuildConfig.OPENWEATHER_API_KEY
            )
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentDeviceLocation(): Result<Location> {
        return Result.failure(NotImplementedError("Device location not implemented yet"))
    }

    override suspend fun getSavedLocations(): Result<List<Location>> {
        return Result.success(emptyList())
    }

    override suspend fun saveLocation(location: Location) {
    }

    override suspend fun removeLocation(location: Location) {
    }
}
