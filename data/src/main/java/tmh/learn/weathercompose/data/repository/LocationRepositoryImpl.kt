package tmh.learn.weathercompose.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import tmh.learn.weathercompose.data.BuildConfig
import tmh.learn.weathercompose.data.mapper.toDomain
import tmh.learn.weathercompose.data.remote.OpenWeatherApi
import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.repository.LocationRepository
import kotlin.coroutines.resume

class LocationRepositoryImpl(
    private val api: OpenWeatherApi,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val appContext: Context
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
        if (!hasLocationPermission()) {
            return Result.failure(SecurityException("Location permission is not granted"))
        }
        return try {
            val location = getLastKnownLocation()
            if (location == null) {
                Result.failure(IllegalStateException("Could not determine current location"))
            } else {
                Result.success(
                    Location(
                        name = "Current Location",
                        country = "",
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSavedLocations(): Result<List<Location>> {
        return Result.success(emptyList())
    }

    override suspend fun saveLocation(location: Location) {
    }

    override suspend fun removeLocation(location: Location) {
    }

    private fun hasLocationPermission(): Boolean {
        val finePermission = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarsePermission = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return finePermission || coarsePermission
    }

    private suspend fun getLastKnownLocation(): android.location.Location? {
        val lastLocation = suspendCancellableCoroutine { continuation ->
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener { continuation.resume(null) }
        }
        if (lastLocation != null) {
            return lastLocation
        }

        val cancellationTokenSource = CancellationTokenSource()
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            )
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener { continuation.resume(null) }
        }
    }
}
