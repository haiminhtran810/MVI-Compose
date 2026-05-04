# WeatherCompose (MVI-Compose)

MVI stands for Model-View-Intent. This project follows an MVI architecture combined with Clean Architecture principles.

## 🏗 Architecture Overview

This project adopts **Clean Architecture** to separate concerns, making the codebase scalable, testable, and maintainable. The project is divided into three main Gradle modules:

### 1. `:domain` (Core Business Logic)
A pure Kotlin module that contains the core business rules. It has no dependencies on the Android framework or specific data delivery implementations (like Retrofit or Room).
* **Models:** Core business data entities.
* **Repositories (Interfaces):** Contract definitions for data operations.
* **Use Cases (Interactors):** Encapsulate specific business rules and actions.

### 2. `:data` (Data Layer)
An Android library module responsible for data retrieval, storage, and external API communication. It implements the repository interfaces defined in the `:domain` module.
* **Remote Data Source:** Network API calls using **Retrofit**.
* **Local Data Source:** Local caching/database using **Room** and preferences via **DataStore**.
* **Repository Implementations:** Coordinates data between local and remote sources, and maps Data Transfer Objects (DTOs) or local entities into Domain models.

### 3. `:app` (Presentation Layer)
The main Android application module containing the UI and user interactions.
* **UI:** Fully built with **Jetpack Compose**.
* **Strict MVI Implementation:**
  * **Model (`UiState`):** An immutable data class representing the single source of truth for the screen at any given time (e.g., `WeatherUiState`). It exclusively contains renderable data, completely free of single-shot event flags.
  * **View (Compose):** Renders the UI by observing the `UiState` and dispatches user actions back to the ViewModel as `Intent`s.
  * **Intent (`WeatherIntent`):** Represents explicit user interactions (e.g., button clicks, text inputs). The ViewModel exposes a single, unified entry point (`processIntent(intent)`) to accept and handle all of these actions.
  * **Effect (`WeatherEffect`):** Single-shot side effects (such as showing Snackbars, navigating, or requesting system permissions) are handled efficiently via Kotlin `Channel`s. They are cleanly decoupled from the persistent `UiState`.
* **ViewModels:** Expose a state flow, process a unified stream of `Intent`s, execute business logic (via Use Cases from the `:domain` layer), dispatch single-shot `Effect`s, and mutate the persistent `UiState`.

## 🛠 Tech Stack & Libraries

* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
* **Design System:** Material Design 3
* **Dependency Injection:** [Koin for Compose](https://insert-koin.io/)
* **Asynchronous Programming:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
* **Networking:** [Retrofit](https://square.github.io/retrofit/) & OkHttp Logging Interceptor
* **Local Storage:** [Room Database](https://developer.android.com/training/data-storage/room) & [Preferences DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/compose/)
* **Navigation:** [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
* **Camera / Hardware:** [CameraX](https://developer.android.com/training/camerax)
