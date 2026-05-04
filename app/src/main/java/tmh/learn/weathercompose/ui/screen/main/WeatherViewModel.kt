package tmh.learn.weathercompose.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.usecase.GetCurrentWeatherUseCase
import tmh.learn.weathercompose.domain.usecase.GetDeviceLocationUseCase
import tmh.learn.weathercompose.domain.usecase.GetForecastUseCase
import tmh.learn.weathercompose.domain.usecase.GetSavedLocationsUseCase
import tmh.learn.weathercompose.domain.usecase.RemoveLocationUseCase
import tmh.learn.weathercompose.domain.usecase.SaveLocationUseCase
import tmh.learn.weathercompose.domain.usecase.SearchLocationUseCase

class WeatherViewModel(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val searchLocationUseCase: SearchLocationUseCase,
    private val getDeviceLocationUseCase: GetDeviceLocationUseCase,
    private val getSavedLocationsUseCase: GetSavedLocationsUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val removeLocationUseCase: RemoveLocationUseCase
) : ViewModel() {

    /**
     * The single source of truth for the View.
     * Represents the current visual state of the weather screen.
     */
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    /**
     * Channel to send single-shot side effects to the View (e.g., show error, request permission).
     * Using a Channel ensures effects are handled exactly once and aren't re-triggered on recomposition.
     */
    private val _effect = Channel<WeatherEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        refreshSavedLocations()
    }

    /**
     * The only entry point for the View to send user intents to the ViewModel.
     * Maps incoming intents to their specific business logic handlers.
     */
    fun processIntent(intent: WeatherIntent) {
        when (intent) {
            is WeatherIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is WeatherIntent.LocationSelected -> onLocationSelected(intent.location)
            is WeatherIntent.RemoveSavedLocation -> onRemoveSavedLocation(intent.location)
            is WeatherIntent.AutoDetectLocationClicked -> onAutoDetectLocationClicked(intent.hasLocationPermission)
            is WeatherIntent.LocationPermissionResult -> onLocationPermissionResult(intent.isGranted)
        }
    }

    private fun onAutoDetectLocationClicked(hasLocationPermission: Boolean) {
        if (!hasLocationPermission) {
            viewModelScope.launch {
                _effect.send(WeatherEffect.RequestLocationPermission)
            }
            return
        }
        loadDeviceLocationAndWeather()
    }

    private fun onLocationPermissionResult(isGranted: Boolean) {
        if (!isGranted) {
            viewModelScope.launch {
                _effect.send(WeatherEffect.ShowError("Location permission is required for auto-detect."))
            }
            return
        }
        loadDeviceLocationAndWeather()
    }

    private fun onSearchQueryChanged(query: String) {
        if (query.length < 2) {
            _uiState.update { it.copy(searchResults = emptyList()) }
            return
        }
        viewModelScope.launch {
            searchLocationUseCase(query)
                .onSuccess { locations ->
                    _uiState.update { it.copy(searchResults = locations) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(searchResults = emptyList()) }
                    _effect.send(WeatherEffect.ShowError(error.message ?: "Failed to search locations."))
                }
        }
    }

    private fun onLocationSelected(location: Location) {
        loadWeatherForLocation(location)
        viewModelScope.launch {
            saveLocationUseCase(location)
            refreshSavedLocations()
        }
    }

    private fun onRemoveSavedLocation(location: Location) {
        viewModelScope.launch {
            removeLocationUseCase(location)
            refreshSavedLocations()
        }
    }

    private fun loadDeviceLocationAndWeather() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            getDeviceLocationUseCase()
                .onSuccess { location ->
                    loadWeatherForLocation(location)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    _effect.send(WeatherEffect.ShowError(error.message ?: "Unable to get device location."))
                }
        }
    }

    private fun loadWeatherForLocation(location: Location) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentLocation = location) }

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
                    forecast = forecast
                )
            }

            error?.message?.let {
                _effect.send(WeatherEffect.ShowError(it))
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
