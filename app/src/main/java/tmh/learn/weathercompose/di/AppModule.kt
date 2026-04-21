package tmh.learn.weathercompose.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tmh.learn.weathercompose.ui.WeatherViewModel

val appModule = module {
    viewModel {
        WeatherViewModel(
            getCurrentWeatherUseCase = get(),
            getForecastUseCase = get(),
            searchLocationUseCase = get(),
            getDeviceLocationUseCase = get(),
            getSavedLocationsUseCase = get(),
            saveLocationUseCase = get(),
            removeLocationUseCase = get()
        )
    }
}
