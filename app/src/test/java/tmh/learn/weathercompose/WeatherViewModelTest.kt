package tmh.learn.weathercompose

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import tmh.learn.weathercompose.domain.entity.DailyForecast
import tmh.learn.weathercompose.domain.entity.Forecast
import tmh.learn.weathercompose.domain.entity.HourlyForecast
import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.entity.Weather
import tmh.learn.weathercompose.domain.repository.LocationRepository
import tmh.learn.weathercompose.domain.repository.WeatherRepository
import tmh.learn.weathercompose.domain.usecase.GetCurrentWeatherUseCase
import tmh.learn.weathercompose.domain.usecase.GetDeviceLocationUseCase
import tmh.learn.weathercompose.domain.usecase.GetForecastUseCase
import tmh.learn.weathercompose.domain.usecase.GetSavedLocationsUseCase
import tmh.learn.weathercompose.domain.usecase.RemoveLocationUseCase
import tmh.learn.weathercompose.domain.usecase.SaveLocationUseCase
import tmh.learn.weathercompose.domain.usecase.SearchLocationUseCase
import tmh.learn.weathercompose.ui.WeatherViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onAutoDetectLocationClicked_withoutPermission_requestsPermission() = runTest {
        val viewModel = createViewModel()

        viewModel.onAutoDetectLocationClicked(hasLocationPermission = false)

        assertTrue(viewModel.uiState.value.requestLocationPermission)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun onLocationPermissionResult_denied_setsError() = runTest {
        val viewModel = createViewModel()

        viewModel.onLocationPermissionResult(isGranted = false)

        assertEquals("Location permission is required for auto-detect.", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.requestLocationPermission)
    }

    @Test
    fun onLocationPermissionResult_granted_loadsLocationWeatherAndForecast() = runTest {
        val location = Location("Ho Chi Minh City", "VN", 10.77, 106.69)
        val weather = Weather(30.0, 34.0, "clear sky", "01d", 70, 3.5, 800)
        val forecast = Forecast(
            hourly = listOf(HourlyForecast(1L, 30.0, "01d", "clear sky")),
            daily = listOf(DailyForecast(1L, 28.0, 33.0, "01d", "clear sky"))
        )

        val viewModel = createViewModel(
            locationRepository = FakeLocationRepository(deviceLocation = Result.success(location)),
            weatherRepository = FakeWeatherRepository(
                currentWeather = Result.success(weather),
                forecast = Result.success(forecast)
            )
        )

        viewModel.onLocationPermissionResult(isGranted = true)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(location, viewModel.uiState.value.currentLocation)
        assertEquals(weather, viewModel.uiState.value.weather)
        assertEquals(forecast, viewModel.uiState.value.forecast)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.errorMessage)
    }

    private fun createViewModel(
        locationRepository: LocationRepository = FakeLocationRepository(),
        weatherRepository: WeatherRepository = FakeWeatherRepository()
    ): WeatherViewModel {
        return WeatherViewModel(
            getCurrentWeatherUseCase = GetCurrentWeatherUseCase(weatherRepository),
            getForecastUseCase = GetForecastUseCase(weatherRepository),
            searchLocationUseCase = SearchLocationUseCase(locationRepository),
            getDeviceLocationUseCase = GetDeviceLocationUseCase(locationRepository),
            getSavedLocationsUseCase = GetSavedLocationsUseCase(locationRepository),
            saveLocationUseCase = SaveLocationUseCase(locationRepository),
            removeLocationUseCase = RemoveLocationUseCase(locationRepository)
        )
    }
}

private class FakeLocationRepository(
    private val deviceLocation: Result<Location> = Result.failure(IllegalStateException("Missing location")),
    private val searchLocations: Result<List<Location>> = Result.success(emptyList()),
    private val savedLocations: Result<List<Location>> = Result.success(emptyList())
) : LocationRepository {
    override suspend fun searchLocation(query: String): Result<List<Location>> = searchLocations

    override suspend fun getCurrentDeviceLocation(): Result<Location> = deviceLocation

    override suspend fun getSavedLocations(): Result<List<Location>> = savedLocations

    override suspend fun saveLocation(location: Location) = Unit

    override suspend fun removeLocation(location: Location) = Unit
}

private class FakeWeatherRepository(
    private val currentWeather: Result<Weather> = Result.failure(IllegalStateException("Missing weather")),
    private val forecast: Result<Forecast> = Result.failure(IllegalStateException("Missing forecast"))
) : WeatherRepository {
    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<Weather> = currentWeather

    override suspend fun getForecast(latitude: Double, longitude: Double): Result<Forecast> = forecast
}
