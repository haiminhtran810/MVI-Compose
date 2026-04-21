package tmh.learn.weathercompose.domain.di

import org.koin.dsl.module
import tmh.learn.weathercompose.domain.usecase.GetCurrentWeatherUseCase
import tmh.learn.weathercompose.domain.usecase.GetDeviceLocationUseCase
import tmh.learn.weathercompose.domain.usecase.GetForecastUseCase
import tmh.learn.weathercompose.domain.usecase.GetSavedLocationsUseCase
import tmh.learn.weathercompose.domain.usecase.RemoveLocationUseCase
import tmh.learn.weathercompose.domain.usecase.SaveLocationUseCase
import tmh.learn.weathercompose.domain.usecase.SearchLocationUseCase

val domainModule = module {
    factory { GetCurrentWeatherUseCase(get()) }
    factory { GetForecastUseCase(get()) }
    factory { SearchLocationUseCase(get()) }
    factory { GetDeviceLocationUseCase(get()) }
    factory { GetSavedLocationsUseCase(get()) }
    factory { SaveLocationUseCase(get()) }
    factory { RemoveLocationUseCase(get()) }
}
