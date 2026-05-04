package tmh.learn.weathercompose.ui.screen.main

import tmh.learn.weathercompose.domain.entity.Forecast
import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.entity.Weather

data class WeatherUiState(
    val isLoading: Boolean = false,
    val requestLocationPermission: Boolean = false,
    val currentLocation: Location? = null,
    val weather: Weather? = null,
    val forecast: Forecast? = null,
    val searchResults: List<Location> = emptyList(),
    val savedLocations: List<Location> = emptyList(),
    val errorMessage: String? = null
)
