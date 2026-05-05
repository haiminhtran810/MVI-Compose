# Naming Conventions for WeatherCompose

This document outlines the standard naming conventions used in this project to maintain consistency and readability.

## Kotlin/Java Code

*   **Classes & Interfaces**: `PascalCase` (e.g., `WeatherActivity`, `MainViewModel`, `WeatherRepository`).
*   **Functions & Methods**: `camelCase` (e.g., `getWeather()`, `fetchData()`).
*   **Variables (Local & Properties)**: `camelCase` (e.g., `weatherList`, `currentTemperature`).
*   **Constants (const val)**: `SCREAMING_SNAKE_CASE` (e.g., `BASE_URL`, `MAX_RETRIES`).
*   **Packages**: `lowercase` without underscores (e.g., `tmh.learn.weathercompose.ui.screen.main`).

## Android Resources

*   **Layout Files**: `what_where.xml` (e.g., `activity_main.xml`, `fragment_weather.xml`, `item_forecast.xml`).
*   **Drawables**: `type_description_state.xml` (e.g., `ic_weather_sunny.xml`, `bg_rounded_corners.xml`).
*   **Strings**: `screen_element_description` (e.g., `weather_text_title`, `main_button_retry`).
*   **Colors**: `color_name` or `element_color` (e.g., `primary_blue`, `text_color_primary`).
*   **Dimensions**: `element_attribute` (e.g., `margin_medium`, `text_size_large`).

## Jetpack Compose

*   **Composable Functions**: 
    *   If returning `Unit` and emitting UI: `PascalCase` (e.g., `WeatherScreen`, `ForecastItem`).
    *   If returning a value and not emitting UI: `camelCase` (e.g., `rememberWeatherState`).
*   **State Variables**: Prefix with `state` or use descriptive names (e.g., `uiState`, `isLoading`).
*   **Previews**: Suffix with `Preview` (e.g., `WeatherScreenPreview`).

## Architecture (MVI)

*   **State**: `ScreenNameUiState` (e.g., `WeatherUiState`).
*   **Event/Intent**: `ScreenNameIntent` (e.g., `WeatherIntent`).
*   **Effect/SideEffect**: `ScreenNameEffect` (e.g., `WeatherEffect`).
*   **ViewModel**: `ScreenNameViewModel` (e.g., `WeatherViewModel`).

## Dependency Injection (Koin)

*   **Modules**: `FeatureNameModule` (e.g., `AppModule`, `DataModule`, `DomainModule`).

## General Rules

*   Use descriptive, intention-revealing names.
*   Avoid single-letter abbreviations except for common loops (e.g., `i`, `j`).
*   Treat acronyms as words in camelCase/PascalCase (e.g., `HttpUrl` instead of `HTTPUrl`, `ApiId` instead of `APIID`).
