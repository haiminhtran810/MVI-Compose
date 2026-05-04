package tmh.learn.weathercompose.ui.screen.main

import tmh.learn.weathercompose.domain.entity.Location

/**
 * Represents all possible user actions or system events (Intents in MVI).
 * The View dispatches these intents to the ViewModel to trigger state changes or side effects.
 */
sealed interface WeatherIntent {
    data class SearchQueryChanged(val query: String) : WeatherIntent
    data class LocationSelected(val location: Location) : WeatherIntent
    data class RemoveSavedLocation(val location: Location) : WeatherIntent
    data class AutoDetectLocationClicked(val hasLocationPermission: Boolean) : WeatherIntent
    data class LocationPermissionResult(val isGranted: Boolean) : WeatherIntent
}

/**
 * Represents single-shot events (Side Effects) that the ViewModel sends back to the View.
 * These are fired exactly once and are not persisted in the UI state.
 * Examples include showing Snackbars, navigating, or requesting permissions.
 */
sealed interface WeatherEffect {
    object RequestLocationPermission : WeatherEffect
    data class ShowError(val message: String) : WeatherEffect
}
