# Project Plan

App Name: WeatherCompose

Create project has  3 modules: 
1. domain (Core layer)
	•	This is the innermost layer (independent, no Android/framework dependencies).
	•	Contains:
	•	UseCases (business logic)
	•	Entities (core models)
	•	Repository interfaces (contracts only)

2. data (Data layer)
	•	Responsible for handling data sources (API, database, cache).
	•	Implements interfaces from the domain layer.
	•	Contains:
	•	Repository implementations
	•	API services (Retrofit, etc.)
	•	Database (Room, etc.)
	•	Mappers (DTO → Domain)

3. app (Presentation layer)
Android-specific layer (UI, ViewModel, Activity/Fragment, Compose).
	•	Depends on domain and data.
	•	Contains:
	•	UI (Compose / XML)
	•	ViewModels
	•	Dependency Injection (Koin)

Dependency Rule (VERY IMPORTANT)
	•	app → depends on → domain, data
	•	data → depends on → domain
	•	domain → depends on → nothing

## Project Brief

# Project Brief: WeatherCompose

## Features
1. **Current Weather Dashboard**:
 Displays real-time weather conditions, including temperature, "feels like" temperature, and localized weather status.
2. **Extended Forecast**: Provides a scrollable hourly breakdown and a multi-day forecast.
3. **Location Search & Auto-Detect**: Allows users to search for global cities by name or use their device's location to automatically fetch relevant weather data.

## High
-Level Tech Stack
* **Language**: Kotlin
* **UI Framework**: Jetpack Compose (strictly following Material Design 3 and
 Edge-to-Edge principles)
* **Architecture**: Clean Architecture (Multi-module: `domain`, `data`, `app
`) + MVI
* **Dependency Injection**: Koin
* **Networking**: Retrofit with OkHttp and Mos
hi (for JSON parsing)
* **Concurrency & Reactive Streams**: Kotlin Coroutines & Flow
* **Code Generation**: KSP (Kotlin Symbol Processing)

## UI Design Image
![UI Design](/Users/belive/Documents
/Belive/Code/WeatherCompose/input_images/weather_app_ui_mockup.png)

Image path = /Users/belive/Documents/Belive/Code/WeatherCompose/input_images/
weather_app_ui_mockup.png

## Implementation Steps
**Total Duration:** 24m 1s

### Task_1_SetupDomain: Create 'domain' module. Define core Entities, Repository interfaces (e.g., WeatherRepository, LocationRepository), and UseCases. Ensure domain has no Android/framework dependencies.
- **Status:** COMPLETED
- **Updates:** The domain module has been created successfully. Core entities (Weather, Forecast, Location), repository interfaces (WeatherRepository, LocationRepository), and use cases (GetCurrentWeatherUseCase, GetForecastUseCase, SearchLocationUseCase, GetDeviceLocationUseCase, GetSavedLocationsUseCase, SaveLocationUseCase, RemoveLocationUseCase) have been defined. The domain module has no Android/framework dependencies and compiles successfully.
- **Acceptance Criteria:**
  - 'domain' module created successfully
  - Entities, Interfaces, and UseCases defined
  - Domain module has no Android dependencies
- **Duration:** 9m 21s

### Task_2_SetupData: Create 'data' module. Implement Repository interfaces from domain. Setup Retrofit API services with OkHttp and Gson. Create DTOs and Mappers (DTO -> Domain). Add API_KEY integration.
- **Status:** COMPLETED
- **Updates:** The user manually implemented the data module using Retrofit with Gson and DataStore, including DTOs, mappers, and repository implementations.
- **Acceptance Criteria:**
  - 'data' module created successfully
  - Data module depends on Domain
  - Retrofit API interface, DTOs and mappers implemented
  - API_KEY integration is implemented as critical acceptance criteria
- **Duration:** 14m 40s

### Task_3_AppLayerViewModelsDI: Configure Koin Dependency Injection. Create ViewModels in the 'app' module. Implement Location tracking (search & auto-detect) handling permissions. Set up app -> domain, data dependencies.
- **Status:** COMPLETED
- **Acceptance Criteria:**
  - Koin dependencies added and DI configured
  - ViewModels expose state for UI
  - Location permission and auto-detect logic implemented
  - App module depends on domain and data
- **StartTime:** 2026-04-17 14:29:20 ICT

### Task_4_ComposeUI: Implement Jetpack Compose UI (Material 3, Edge-to-Edge). Build Current Weather Dashboard, Extended Forecast, and Location Search. Apply vibrant colors.
- **Status:** COMPLETED
- **Updates:** Implemented and refined a richer Material 3 Compose weather screen in the app module with a vibrant current weather hero card, location search results, hourly forecast row, weather detail metric cards, daily forecast list, and saved locations chips. UI wiring consumes ViewModel state end-to-end and supports auto-detect location, search selection, saved location selection, and saved location removal.
- **Acceptance Criteria:**
  - UI implemented using Jetpack Compose
  - Material 3 and Edge-to-Edge applied
  - The implemented UI must match the design provided in /Users/belive/Documents/Belive/Code/WeatherCompose/input_images/weather_app_ui_mockup.jpg

### Task_5_RunAndVerify: Final Polish, Add App Icon, Run and Verify. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Adaptive app icon added
  - project builds successfully, api working, integration of feature x with UI
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - The implemented UI must match the design provided in /Users/belive/Documents/Belive/Code/WeatherCompose/input_images/weather_app_ui_mockup.jpg

