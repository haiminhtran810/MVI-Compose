# Architecture Guidelines for WeatherCompose

This project follows **Clean Architecture** combined with the **MVI (Model-View-Intent)** presentation pattern and **Jetpack Compose** for the UI.

## 1. Multi-Module Structure (Clean Architecture)

The project is divided into three main modules:

*   **`:domain` (Domain Layer)**
    *   **Nature**: Pure Kotlin/Java module. No Android framework dependencies.
    *   **Contents**: Business logic, Use Cases (Interactors), Domain Models (Entities), and Repository Interfaces.
    *   **Dependencies**: Depends on nothing. It is the innermost core of the application.

*   **`:data` (Data Layer)**
    *   **Nature**: Android Library module.
    *   **Contents**: Repository implementations, Data Sources (Remote/API, Local/Database), DTOs (Data Transfer Objects), and mappers (to map DTOs to Domain Models).
    *   **Dependencies**: Depends on the `:domain` module.

*   **`:app` (Presentation Layer)**
    *   **Nature**: Android App module.
    *   **Contents**: Jetpack Compose UI (Activities, Screens, Components), ViewModels, MVI components (State, Event, Effect), and Dependency Injection graph wiring.
    *   **Dependencies**: Depends on `:domain` and `:data` (primarily for DI wiring).

## 2. Presentation Layer: MVI Pattern

We use the MVI (Model-View-Intent) pattern to manage UI state predictably.

*   **State (Model)**: A single, immutable Data Class representing the entire state of a given screen. 
    *   *Rule*: The UI observes this state and recomposes whenever it changes.
*   **Event (Intent)**: A Sealed Class/Interface representing all user actions or system events that the ViewModel needs to handle (e.g., `OnRefresh`, `OnCityClicked`).
    *   *Rule*: The UI sends Events to the ViewModel.
*   **Effect (Side Effect)**: A Sealed Class/Interface representing one-off events that shouldn't be persisted in the state, such as navigation, showing a Toast, or displaying a Snackbar.
    *   *Rule*: The ViewModel emits Effects; the UI collects them and reacts exactly once.
*   **ViewModel**: 
    *   Receives Events from the UI.
    *   Executes business logic via Use Cases from the `:domain` module.
    *   Updates the State via a Reducer.
    *   Fires Effects when necessary.

## 3. Dependency Rule

*   Dependencies must always point inwards toward the Domain Layer.
*   The Data Layer and Presentation Layer depend on the Domain Layer, but the Domain Layer does not know about them.
*   Data flowing across boundaries must be mapped to the appropriate layer's model (e.g., Network DTOs must be mapped to Domain Models in the Data layer before being returned to the Domain/Presentation layers).

## 4. Jetpack Compose Rules

*   **Unidirectional Data Flow (UDF)**: State flows down (from ViewModel to Composables), and events flow up (from Composables to ViewModel).
*   **State Hoisting**: Keep Composables as stateless as possible by passing state down as parameters and hoisting events up using lambdas.
*   **Previews**: Build small, isolated, and stateless Composables that are easily previewable.
