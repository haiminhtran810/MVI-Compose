package tmh.learn.weathercompose.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tmh.learn.weathercompose.domain.entity.Forecast
import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.entity.Weather
import tmh.learn.weathercompose.domain.usecase.GetCurrentWeatherUseCase
import tmh.learn.weathercompose.domain.usecase.GetDeviceLocationUseCase
import tmh.learn.weathercompose.domain.usecase.GetForecastUseCase
import tmh.learn.weathercompose.domain.usecase.GetSavedLocationsUseCase
import tmh.learn.weathercompose.domain.usecase.RemoveLocationUseCase
import tmh.learn.weathercompose.domain.usecase.SaveLocationUseCase
import tmh.learn.weathercompose.domain.usecase.SearchLocationUseCase

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

class WeatherViewModel(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val searchLocationUseCase: SearchLocationUseCase,
    private val getDeviceLocationUseCase: GetDeviceLocationUseCase,
    private val getSavedLocationsUseCase: GetSavedLocationsUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val removeLocationUseCase: RemoveLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        refreshSavedLocations()
    }

    fun onAutoDetectLocationClicked(hasLocationPermission: Boolean) {
        if (!hasLocationPermission) {
            _uiState.update {
                it.copy(
                    requestLocationPermission = true,
                    errorMessage = null
                )
            }
            return
        }
        loadDeviceLocationAndWeather()
    }

    fun onLocationPermissionResult(isGranted: Boolean) {
        if (!isGranted) {
            _uiState.update {
                it.copy(
                    requestLocationPermission = false,
                    errorMessage = "Location permission is required for auto-detect."
                )
            }
            return
        }
        loadDeviceLocationAndWeather()
    }

    fun onPermissionRequestConsumed() {
        _uiState.update { it.copy(requestLocationPermission = false) }
    }

    fun onSearchQueryChanged(query: String) {
        if (query.length < 2) {
            _uiState.update { it.copy(searchResults = emptyList(), errorMessage = null) }
            return
        }
        viewModelScope.launch {
            searchLocationUseCase(query)
                .onSuccess { locations ->
                    _uiState.update { it.copy(searchResults = locations, errorMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            searchResults = emptyList(),
                            errorMessage = error.message ?: "Failed to search locations."
                        )
                    }
                }
        }
    }

    fun onLocationSelected(location: Location) {
        loadWeatherForLocation(location)
        viewModelScope.launch {
            saveLocationUseCase(location)
            refreshSavedLocations()
        }
    }

    fun onRemoveSavedLocation(location: Location) {
        viewModelScope.launch {
            removeLocationUseCase(location)
            refreshSavedLocations()
        }
    }

    private fun loadDeviceLocationAndWeather() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    requestLocationPermission = false,
                    errorMessage = null
                )
            }
            getDeviceLocationUseCase()
                .onSuccess { location ->
                    loadWeatherForLocation(location)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to get device location."
                        )
                    }
                }
        }
    }

    private fun loadWeatherForLocation(location: Location) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentLocation = location, errorMessage = null) }

            val weatherDeferred = async {
                getCurrentWeatherUseCase(location.latitude, location.longitude)
            }
            val forecastDeferred = async {
                getForecastUseCase(location.latitude, location.longitude)
            }

            val weatherResult = weatherDeferred.await()
            val forecastResult = forecastDeferred.await()

            val weather = weatherResult.getOrNull()
            val forecast = forecastResult.getOrNull()
            val error = weatherResult.exceptionOrNull() ?: forecastResult.exceptionOrNull()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    weather = weather,
                    forecast = forecast,
                    errorMessage = error?.message
                )
            }
        }
    }

    private fun refreshSavedLocations() {
        viewModelScope.launch {
            getSavedLocationsUseCase()
                .onSuccess { locations ->
                    _uiState.update { it.copy(savedLocations = locations) }
                }
                .onFailure {
                    _uiState.update { it.copy(savedLocations = emptyList()) }
                }
        }
    }
}
