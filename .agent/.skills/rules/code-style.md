# Code Style Guidelines for WeatherCompose

This project generally follows the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) and [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).

## 1. General Formatting
* **Indentation**: Use 4 spaces for typical blocks, 8 spaces for line continuations.
* **Line Length**: Aim for a maximum of 100-120 characters per line.
* **Braces**: Use standard K&R style (opening brace on the same line as the declaration).
* **Blank Lines**: Leave a single blank line between logical blocks of code and top-level declarations to improve readability.

## 2. Kotlin Specifics
* **Immutability**: Always prefer `val` over `var` unless the variable inherently needs to be mutable. Prefer immutable collections (`List`, `Map`) over mutable ones (`MutableList`, `MutableMap`) unless mutation is required.
* **Nullability**: Leverage Kotlin's null safety. Avoid using `!!` (the not-null assertion operator) unless absolutely necessary. Use safe calls (`?.`) and Elvis operators (`?:`) instead.
* **Expression Bodies**: Use expression bodies for simple, single-statement functions to keep the code concise.
  * *Good*: `fun getGreeting() = "Hello"`
  * *Bad*: `fun getGreeting(): String { return "Hello" }`
* **Named Arguments**: Use named arguments when calling functions with 3 or more parameters or when consecutive parameters have the same type, to avoid confusion.

## 3. Jetpack Compose Code Style
* **Modifiers**: 
  * Always pass a `Modifier` as the first optional parameter to public Composable functions. Default it to `Modifier`.
  * *Modifier Ordering*: Order matters. Keep the chain logical. Typical order: `Size` -> `Background`/`Padding` -> `Interactive` (clickable) -> `Layout` specific constraints.
* **State Hoisting**: Composables should preferably be stateless. Pass the required state in as parameters, and pass events out via lambdas.
* **Small & Focused**: Keep Composables small and single-purpose. If a Composable is getting too long (e.g., > 100 lines), break it down into smaller, private Composables.

## 4. Clean Code Practices
* **Function Size**: Keep functions small and focused on a single task.
* **Comments**: Code should be self-documenting through clear, expressive naming. Use comments to explain *why* a particular approach was taken (the intent), not *what* the code does. 
* **KDoc**: Use KDoc comments for public APIs, core Domain entities, and complex business logic.
* **Error Handling**: Use `Result` or custom sealed classes for predictable error handling between the Data, Domain, and Presentation layers. Avoid throwing raw exceptions into the UI layer.

## 5. Coroutines & Asynchrony
* **Dispatchers**: Do not hardcode dispatchers (like `Dispatchers.IO`) inside your Domain or Data logic. Inject them or pass them as parameters to make the logic easily testable.
* **Lifecycle Awareness**: Always launch coroutines within a lifecycle-aware scope (e.g., `viewModelScope` in ViewModels, or `lifecycleScope` in Fragments/Activities).