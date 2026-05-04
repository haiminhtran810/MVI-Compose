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
* **MVI Implementation:**
  * **Model (UI State):** An immutable data class representing the single source of truth for the screen at any given time.
  * **View (Compose):** Renders the UI reacting to state emissions.
  * **Intent (Action/Event):** Represents user interactions (e.g., button clicks, text input) or system events that are dispatched to the ViewModel.
* **ViewModels:** Consume intents, execute business logic (via Use Cases from the `:domain` layer), and reduce the results into a new UI State.

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
